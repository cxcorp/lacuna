package cx.corp.lacuna.ui.presenter;

import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.core.domain.NativeProcessImpl;
import cx.corp.lacuna.ui.model.ProcessListModel;
import cx.corp.lacuna.ui.view.ProcessListView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.swing.plaf.OptionPaneUI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class ProcessListPresenterTest {

    private ProcessListPresenter presenter;
    @Mock
    private ProcessListModel model;
    @Mock
    private ProcessListView view;

    @Captor
    private ArgumentCaptor<NativeProcess> processChosenCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new ProcessListPresenter(view, model);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ctorThrowsIfViewNull() {
        new ProcessListPresenter(null, model);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ctorThrowsIfModelNull() {
        new ProcessListPresenter(view, null);
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

    @Test
    public void initializeAttachesPresenterToView() {
        presenter.initialize();
        verify(view).attach(presenter);
    }

    @Test
    public void processChosenDoesntThrowIfViewReturnsEmptyAndNoListenersRegistered() {
        given(view.getChosenProcess()).willReturn(Optional.empty());
        presenter.processChosen();
    }

    @Test(expected = IllegalArgumentException.class)
    public void addProcessChosenListenerThrowsWhenNullArgPassed() {
        presenter.addProcessChosenListener(null);
    }

    @Test
    public void processChosenDoesntCallListenerIfViewReturnsEmpty() {
        given(view.getChosenProcess()).willReturn(Optional.empty());
        presenter.addProcessChosenListener(
            i -> fail("Callback was called even through view returned empty!"));
        presenter.processChosen();
    }

    @Test
    public void processChosenCallsOneListenerWithCorrectPid() {
        NativeProcess proc = randomNativeProcess();
        given(view.getChosenProcess()).willReturn(Optional.of(proc));
        ProcessChosenEventListener callback = mock(ProcessChosenEventListener.class);
        doNothing().when(callback).processChosen(proc);

        presenter.addProcessChosenListener(callback);
        presenter.processChosen();

        verify(callback).processChosen(processChosenCaptor.capture());
        assertEquals(proc, processChosenCaptor.getValue());
    }

    private static NativeProcess randomNativeProcess() {
        Random rng = ThreadLocalRandom.current();
        NativeProcess proc = new NativeProcessImpl();
        proc.setPid(1 + rng.nextInt(32000));
        proc.setOwner(randomString(5, 20));
        proc.setDescription(randomString(10, 60));
        return proc;
    }

    private static String randomString(int minSize, int maxSize) {
        Random rng = ThreadLocalRandom.current();
        StringBuilder sb = new StringBuilder(maxSize);
        for (int i = 0; i < maxSize; i++) {
            sb.append((char)rng.nextInt(('-' - 'a') + 'a'));
        }
        return sb.toString();
    }
}
