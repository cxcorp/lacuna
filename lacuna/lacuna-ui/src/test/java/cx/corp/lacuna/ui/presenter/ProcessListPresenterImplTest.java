package cx.corp.lacuna.ui.presenter;

import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.core.domain.NativeProcessImpl;
import cx.corp.lacuna.ui.model.ProcessListModel;
import cx.corp.lacuna.ui.view.ProcessListView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class ProcessListPresenterImplTest {

    private ProcessListPresenterImpl presenter;
    @Mock
    private ProcessListModel model;
    @Mock
    private ProcessListView view;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new ProcessListPresenterImpl(view, model);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ctorThrowsIfViewNull() {
        new ProcessListPresenterImpl(null, model);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ctorThrowsIfModelNull() {
        new ProcessListPresenterImpl(view, null);
    }

    @Test
    public void updateRequestedUpdatesProcessesInViewWithEmptyList() {
        given(model.getProcesses()).willReturn(new ArrayList<>());
        presenter.updateRequested();
        verify(view).setProcessList(model.getProcesses());
    }

    @Test
    public void updateRequestedUpdatesProcessesInViewWithNonEmptyList() {
        List<NativeProcess> procs = new ArrayList<>();
        procs.add(new NativeProcessImpl(123, "123", "123"));
        procs.add(new NativeProcessImpl(555, "5555", "555"));
        procs.add(new NativeProcessImpl(521, "1235", "999"));
        given(model.getProcesses()).willReturn(procs);
        presenter.updateRequested();
        verify(view).setProcessList(model.getProcesses());
    }
}
