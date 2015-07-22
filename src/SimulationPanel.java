import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

public class SimulationPanel extends JPanel implements ActionListener {
    private Simulation sim;
    private boolean isCancelled = false;
    private int finalMapSize = -1;
    ControlPanel buttonsAndSuch;
    MapPanel map;

    public SimulationPanel(Simulation sim, int preferred_side_length) {
        super(new BorderLayout(0, 5));
        this.sim = sim;
        finalMapSize = (int)((sim.getNumIntersectionsPerSide() + 1)*(Intersection.length +
                    Intersection.roadLength) + Intersection.length*(sim.getNumIntersectionsPerSide() - 1));
        map = new MapPanel(sim, preferred_side_length, finalMapSize);
        add(map, BorderLayout.CENTER);
        buttonsAndSuch = new ControlPanel();
        buttonsAndSuch.cancelButton.addActionListener(this);
        add(buttonsAndSuch, BorderLayout.PAGE_END);
        SimulationTask simTask = new SimulationTask(sim, map);
        simTask.execute();
        /*boolean finished = false;
        while (!finished)  {
            buttonsAndSuch.progressBar.setValue(simTask.getProgress());
            if (simTask.getProgress() == 100) {
                finished = true;
            }
        }
        */
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonsAndSuch.cancelButton) {
            isCancelled = true;
        }
    }

    class ControlPanel extends JPanel {
        JButton cancelButton;
        JProgressBar progressBar;
        JLabel statusLabel;
        public ControlPanel() {
            cancelButton = new JButton("Cancel");
            progressBar = new JProgressBar();
            statusLabel = new JLabel("Status: Running");
            add(cancelButton);
            add(progressBar);
            add(statusLabel);
        }
    }

    class SimulationTask extends SwingWorker<Double, Integer> {
        Simulation mySim;
        MapPanel map;
        SimulationTask(Simulation sim, MapPanel map) {
            this.mySim = sim;
            this.map = map;
        }
        @Override
        public Double doInBackground() {
            int iterationsSincePaint = 0;
            while (sim.getTimeElapsed() < sim.getSimulationTime()) {
                if (isCancelled) {
                    buttonsAndSuch.progressBar.setValue(0);
                    buttonsAndSuch.statusLabel.setText("Status: Cancelled");
                    return Double.NaN;
                }
                mySim.updateSim();
                iterationsSincePaint++;
                try {
    				Thread.sleep(100 - (System.currentTimeMillis() % 100));
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
                setProgress((int)(100*sim.getTimeElapsed()/sim.getSimulationTime()));
                buttonsAndSuch.progressBar.setValue(getProgress());
                if (iterationsSincePaint == 1) {
                    repaint();
                    iterationsSincePaint = 0;
                }
            }
            buttonsAndSuch.statusLabel.setText("Status: Done");
            return mySim.averageSpeed();
        }

        public void done() {
        }

        /*@Override
        protected void process(List<Integer> chunks) {
            for (int number : chunks) {
                textArea.append(number + "\n");
            }
        }
        */
    }
}
