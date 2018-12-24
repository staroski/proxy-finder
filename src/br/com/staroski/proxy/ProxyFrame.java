package br.com.staroski.proxy;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
final class ProxyFrame extends JFrame {

    private class ProxyTableModel extends AbstractTableModel {

        private ProxySetting[] proxies;

        public ProxyTableModel(ProxySetting[] proxies) {
            this.proxies = proxies;
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return "Name";
                case 1:
                    return "Host";
                case 2:
                    return "Port";
            }
            return null;
        }

        @Override
        public int getRowCount() {
            return proxies.length;
        }

        @Override
        public Object getValueAt(int row, int column) {
            if (row >= 0 && row < proxies.length) {
                ProxySetting proxy = proxies[row];
                switch (column) {
                    case 0:
                        return proxy.name;
                    case 1:
                        return proxy.host;
                    case 2:
                        return proxy.port;
                }
            }
            return null;
        }

    }

    ProxyFrame(ProxyFinder finder) throws Exception {
        super("Staroski's Proxy Finder");
        InputStream input = getClass().getResourceAsStream("/site-smiley_48x48.png");
        BufferedImage image = ImageIO.read(input);
        setIconImage(image);
        Dimension size = new Dimension(480, 320);
        setSize(size);
        setMinimumSize(size);
        ProxySetting[] proxies = finder.find();
        ProxyTableModel tableModel = new ProxyTableModel(proxies);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("The following proxies were found:"), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        setContentPane(panel);
    }
}
