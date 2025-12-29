package dev.phonchai.datetime.picker.slider;

import com.formdev.flatlaf.util.Animator;
import com.formdev.flatlaf.util.CubicBezierEasing;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PanelSlider extends JLayeredPane {

    public Component getSlideComponent() {
        return slideComponent;
    }

    private PanelSnapshot panelSnapshot;
    private Component slideComponent;

    public PanelSlider() {
        init();
    }

    private void init() {
        panelSnapshot = new PanelSnapshot();
        setLayout(new CardLayout());
        setLayer(panelSnapshot, JLayeredPane.DRAG_LAYER);
        add(panelSnapshot);
        panelSnapshot.setVisible(false);
    }

    public void addSlide(Component component, SliderTransition transition) {
        this.slideComponent = component;
        if (getComponentCount() == 1) {
            add(component);
            repaint();
            revalidate();
            component.setVisible(true);
        } else {
            Component oldComponent = getComponent(1);
            add(component);
            if (transition != null && Animator.useAnimation()) {
                doLayout();
                component.doLayout();
                Image oldImage = panelSnapshot.createSnapshotOld(oldComponent);
                Image newImage = panelSnapshot.createSnapshotNew(component);
                remove(oldComponent);
                panelSnapshot.animate(component, transition, oldImage, newImage);
            } else {
                component.setVisible(true);
                remove(oldComponent);
                revalidate();
                repaint();
            }
        }
    }

    private static class PanelSnapshot extends JComponent {

        private final Animator animator;
        private Component component;
        private float animate;

        private SliderTransition sliderTransition;
        private Image oldImage;
        private Image newImage;
        private BufferedImage oldBuffer;
        private BufferedImage newBuffer;

        public PanelSnapshot() {
            animator = new Animator(260, new Animator.TimingTarget() {
                @Override
                public void timingEvent(float v) {
                    animate = v;
                    repaint();
                }

                @Override
                public void end() {
                    setVisible(false);
                    if (component != null) {
                        component.setVisible(true);
                    }
                }
            });
            animator.setResolution(10);
            animator.setInterpolator(CubicBezierEasing.EASE_OUT);
        }

        private BufferedImage createSnapshot(Component component, BufferedImage buffer) {
            int width = getWidth();
            int height = getHeight();
            if (width <= 0 || height <= 0 || component == null) {
                return null;
            }
            if (buffer == null || buffer.getWidth() != width || buffer.getHeight() != height) {
                buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            } else {
                Graphics2D clear = buffer.createGraphics();
                try {
                    clear.setComposite(AlphaComposite.Clear);
                    clear.fillRect(0, 0, width, height);
                } finally {
                    clear.dispose();
                }
            }

            Graphics2D g2 = buffer.createGraphics();
            try {
                component.printAll(g2);
            } finally {
                g2.dispose();
            }
            return buffer;
        }

        private Image createSnapshotOld(Component component) {
            oldBuffer = createSnapshot(component, oldBuffer);
            return oldBuffer;
        }

        private Image createSnapshotNew(Component component) {
            newBuffer = createSnapshot(component, newBuffer);
            return newBuffer;
        }

        protected void animate(Component component, SliderTransition sliderTransition, Image oldImage, Image newImage) {
            if (animator.isRunning()) {
                animator.stop();
            }
            this.component = component;
            this.oldImage = oldImage;
            this.newImage = newImage;
            this.sliderTransition = sliderTransition;
            this.animate = 0f;
            repaint();
            setVisible(true);
            if (component != null) {
                component.setVisible(false);
            }
            animator.start();
        }

        @Override
        public void paint(Graphics g) {
            if (sliderTransition != null) {
                int width = getWidth();
                int height = getHeight();
                sliderTransition.render(g, oldImage, newImage, width, height, animate);
            }
        }
    }
}
