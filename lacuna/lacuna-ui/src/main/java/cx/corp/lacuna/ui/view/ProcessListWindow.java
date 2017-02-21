package cx.corp.lacuna.ui.view;

import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.ui.presenter.ProcessListCallbacks;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class ProcessListWindow implements ProcessListView {

    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGHT = 400;
    private static final int COMPONENT_PADDING = 16;
    private static final int TABLE_PADDING_X = 16;
    private static final int TABLE_PADDING_Y = 0; // doesn't resize rows
    private static final int COLUMN_INDEX_PID = 0;

    private final Window modalParent;

    private ProcessListCallbacks callbacks;
    private JDialog root;
    private JPanel frameContainer;
    private NativeProcessNonEditTableModel tableModel;
    private JTable table;
    private JTextField searchField;
    private JButton searchButton;
    private JButton searchClearButton;
    private JButton chooseButton;
    private JButton updateButton;

    private List<NativeProcess> processes;
    private NativeProcess chosenProcess;
    private String searchFilter;

    public ProcessListWindow(Window modalParent) {
        this.modalParent = modalParent;
        chosenProcess = null;
        searchFilter = "";
        createWindow();
    }

    public void show() {
        root.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                if (callbacks != null) {
                    callbacks.updateRequested();
                }
                super.windowOpened(e);
            }
        });
        root.setVisible(true); // dialog, blocks after this call
    }

    @Override
    public Optional<NativeProcess> getChosenProcess() {
        return Optional.ofNullable(chosenProcess);
    }

    @Override
    public void setProcessList(List<NativeProcess> processes) {
        this.processes = processes;
        clearTable();
        addRows(processes);
    }

    @Override
    public void attach(ProcessListCallbacks processListCallbacks) {
        callbacks = processListCallbacks;
    }

    private void createWindow() {
        createFrame();
        createComponents();
        root.pack();
    }

    private void createFrame() {
        root = new JDialog(modalParent, "Process list", Dialog.ModalityType.APPLICATION_MODAL);
        root.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        root.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
    }

    private void createComponents() {
        createAndSetContentPane();
        createTable();
        createOtherControls();
        // Bind things only after all components are created because
        // some action listeners depend on other components which
        // might not be initialized yet
        createBindingsAndListeners();
    }

    private void createAndSetContentPane() {
        frameContainer = new JPanel(new GridBagLayout());
        root.setContentPane(frameContainer);
    }

    private void createTable() {
        createTableModel();
        table = new JTable(tableModel);
        table.setIntercellSpacing(new Dimension(TABLE_PADDING_X, TABLE_PADDING_Y));
        sortPidColumnDescending();

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 0.95;
        c.insets = new Insets(COMPONENT_PADDING, COMPONENT_PADDING, 0, COMPONENT_PADDING);
        JScrollPane scrollPane = new JScrollPane(table);
        frameContainer.add(scrollPane, c);
    }

    private void createTableModel() {
        NativeProcessNonEditTableModel model = new NativeProcessNonEditTableModel();
        model.addColumn("PID");
        model.addColumn("Owner");
        model.addColumn("Description");
        tableModel = model;
    }

    private void sortPidColumnDescending() {
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(tableModel);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(COLUMN_INDEX_PID, SortOrder.DESCENDING));
        sorter.setSortKeys(sortKeys);
        table.setRowSorter(sorter);
    }

    // @formatter:off
    private void createOtherControls() {
        JPanel controlsPanel = new JPanel(new BorderLayout());

            JPanel searchAndUpdatePanel = new JPanel(new BorderLayout());
                JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
                    searchField = new JTextField();
                    searchField.setColumns(20);
                    searchPanel.add(searchField);
                    searchButton = new JButton("Search");
                    searchPanel.add(searchButton);
                    searchClearButton = new JButton("Clear");
                    searchPanel.add(searchClearButton);
                searchAndUpdatePanel.add(searchPanel, BorderLayout.WEST);

                JPanel updatePanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
                    updateButton = new JButton("Refresh");
                    updatePanel.add(updateButton);
                searchAndUpdatePanel.add(updatePanel);
            controlsPanel.add(searchAndUpdatePanel, BorderLayout.NORTH);

            JPanel choosePanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
                chooseButton = new JButton("Choose process");
                chooseButton.setEnabled(false); // disabled until a process is selected
                choosePanel.add(chooseButton);
            controlsPanel.add(choosePanel, BorderLayout.SOUTH);

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 1;
        c.weighty = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(COMPONENT_PADDING, COMPONENT_PADDING, COMPONENT_PADDING, COMPONENT_PADDING);
        frameContainer.add(controlsPanel, c);
    }
    // @formatter:on

    private void createBindingsAndListeners() {
        chooseButtonIsEnabledWhenRowIsSelected();
        rowSelectionUpdatesChosenProcessId();
        searchFieldUpdatesSearchFilter();
        chooseButtonSendsMessageAndClosesWindow();
        searchButtonUpdatesFilter();
        searchClearButtonClearsSearchField();
        updateButtonRequestsUpdate();
    }

    private void chooseButtonIsEnabledWhenRowIsSelected() {
        table.getSelectionModel().addListSelectionListener(
            e -> chooseButton.setEnabled(true));
    }

    private void rowSelectionUpdatesChosenProcessId() {
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                chosenProcess = null;
                return;
            }
            // Model index is the same as the `processes` index, only the view index
            // is affected by sorting and filtering
            int modelIndex = table.convertRowIndexToModel(row);
            chosenProcess = processes.get(modelIndex);
        });
    }

    private void searchFieldUpdatesSearchFilter() {
        UpdateDocumentListener.addTo(searchField, text -> searchFilter = text);
    }

    private void chooseButtonSendsMessageAndClosesWindow() {
        chooseButton.addActionListener(e -> {
            if (callbacks != null) {
                callbacks.processChosen();
            }
            root.setVisible(false);
        });
    }

    private void searchButtonUpdatesFilter() {
        searchButton.addActionListener(e -> updateTableFilter());
    }

    @SuppressWarnings("unchecked")
    private void updateTableFilter() {
        TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>)table.getRowSorter();
        sorter.setRowFilter(searchFilter.isEmpty() ? null : RowFilter.regexFilter(searchFilter));
    }

    private void searchClearButtonClearsSearchField() {
        searchClearButton.addActionListener(e -> {
            searchField.setText("");
            searchFilter = "";
            updateTableFilter();
        });
    }

    private void updateButtonRequestsUpdate() {
        updateButton.addActionListener(e -> {
            if (callbacks != null) {
                callbacks.updateRequested();
            }
        });
    }

    private void clearTable() {
        tableModel.setRowCount(0);
    }

    private void addRows(Collection<NativeProcess> processes) {
        processes.forEach(this.tableModel::addRow);
        new TableColumnResizer(table).resize();
    }
}
