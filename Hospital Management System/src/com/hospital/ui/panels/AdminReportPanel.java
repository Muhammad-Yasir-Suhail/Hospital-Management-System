package com.hospital.ui.panels;

import com.hospital.controller.DoctorController;
import com.hospital.controller.PatientController;
import com.hospital.model.User;
import com.hospital.utils.DBConnection;
import com.hospital.utils.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

public class AdminReportPanel extends JPanel {
    private final User user;
    private final JPanel reportContent = new JPanel();

    private final JLabel totalPatientsValue = metricValue();
    private final JLabel activeDoctorsValue = metricValue();
    private final JLabel monthlyAppointmentsValue = metricValue();
    private final JLabel completedAppointmentsValue = metricValue();
    private final JLabel unpaidBillsValue = metricValue();
    private final JLabel monthlyRevenueValue = metricValue();

    private final BarChartPanel appointmentsChart = new BarChartPanel("Appointments by Month", "Last 6 months", UITheme.PRIMARY, false);
    private final BarChartPanel revenueChart = new BarChartPanel("Revenue by Month", "Paid billing totals", UITheme.SECONDARY, true);
    private final BarChartPanel appointmentStatusChart = new BarChartPanel("Appointment Status", "Current appointment mix", UITheme.PRIMARY_DARK, false);
    private final BarChartPanel billingStatusChart = new BarChartPanel("Billing Status", "Current payment mix", UITheme.SUCCESS, false);

    private final DefaultTableModel activityModel = new DefaultTableModel(
            new String[]{"Time", "Module", "Record", "User", "Description"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final DecimalFormat countFormat = new DecimalFormat("#,##0");
    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0.00");
    private final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH);
    private final DateTimeFormatter activityFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale.ENGLISH);

    public AdminReportPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);

        JPanel header = createHeader();
        add(header, BorderLayout.NORTH);

        reportContent.setOpaque(false);
        reportContent.setLayout(new BoxLayout(reportContent, BoxLayout.Y_AXIS));
        reportContent.setBorder(new EmptyBorder(18, 18, 18, 18));

        reportContent.add(sectionTitle("Key Metrics"));
        reportContent.add(createSummaryGrid());
        reportContent.add(Box.createVerticalStrut(18));
        reportContent.add(sectionTitle("Charts"));
        reportContent.add(createChartsGrid());
        reportContent.add(Box.createVerticalStrut(18));
        reportContent.add(sectionTitle("Recent Activity"));
        reportContent.add(createActivityCard());

        JScrollPane scrollPane = new JScrollPane(reportContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);

        loadReport();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint paint = new GradientPaint(0, 0, UITheme.BG_TOP, 0, getHeight(), UITheme.BG_BOTTOM);
        g2.setPaint(paint);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(16, 8));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(18, 18, 10, 18));

        JPanel textBlock = new JPanel();
        textBlock.setOpaque(false);
        textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Admin Reports");
        title.setFont(UITheme.HEADER_FONT.deriveFont(Font.BOLD, 24f));
        title.setForeground(UITheme.PRIMARY_DARK);

        JLabel subtitle = new JLabel("Monthly trends, status breakdowns, and recent audit activity for the admin team.");
        subtitle.setFont(UITheme.BODY_FONT.deriveFont(Font.PLAIN, 14f));
        subtitle.setForeground(new Color(85, 95, 110));

        textBlock.add(title);
        textBlock.add(Box.createVerticalStrut(4));
        textBlock.add(subtitle);

        JButton refresh = styledButton("Refresh Report", UITheme.PRIMARY);
        refresh.addActionListener(e -> loadReport());

        JButton print = styledButton("Print Report", UITheme.SECONDARY);
        print.addActionListener(e -> printReport());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        actions.add(refresh);
        actions.add(print);

        header.add(textBlock, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private JComponent sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.BUTTON_FONT.deriveFont(Font.BOLD, 15f));
        label.setForeground(UITheme.PRIMARY_DARK);
        label.setBorder(new EmptyBorder(0, 4, 10, 4));
        return label;
    }

    private JPanel createSummaryGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 3, 16, 16));
        grid.setOpaque(false);

        grid.add(metricCard("Total Patients", totalPatientsValue, UITheme.PRIMARY));
        grid.add(metricCard("Active Doctors", activeDoctorsValue, UITheme.SECONDARY));
        grid.add(metricCard("Appointments This Month", monthlyAppointmentsValue, UITheme.PRIMARY_DARK));
        grid.add(metricCard("Completed Appointments", completedAppointmentsValue, UITheme.SUCCESS));
        grid.add(metricCard("Unpaid Bills", unpaidBillsValue, UITheme.ERROR));
        grid.add(metricCard("Monthly Revenue", monthlyRevenueValue, new Color(122, 85, 0)));

        return grid;
    }

    private JPanel createChartsGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
        grid.setOpaque(false);

        grid.add(appointmentsChart);
        grid.add(revenueChart);
        grid.add(appointmentStatusChart);
        grid.add(billingStatusChart);

        return grid;
    }

    private JPanel createActivityCard() {
        JPanel card = new RoundedCard();
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("Latest audit log entries");
        title.setFont(UITheme.BODY_FONT.deriveFont(Font.BOLD, 14f));
        title.setForeground(UITheme.PRIMARY_DARK);

        JTable table = new JTable(activityModel);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(UITheme.BODY_FONT.deriveFont(Font.PLAIN, 13f));
        table.setSelectionBackground(new Color(220, 236, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setFillsViewportHeight(true);
        table.setBackground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setFont(UITheme.BUTTON_FONT);
        header.setBackground(UITheme.PRIMARY);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 235, 240)));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        card.add(title, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private JPanel metricCard(String titleText, JLabel valueLabel, Color accent) {
        JPanel card = new RoundedCard();
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(220, 145));

        JLabel title = new JLabel(titleText);
        title.setFont(UITheme.BODY_FONT.deriveFont(Font.BOLD, 15f));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valueLabel.setFont(UITheme.HEADER_FONT.deriveFont(Font.BOLD, 34f));
        valueLabel.setForeground(UITheme.PRIMARY_DARK);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(10, 10, 10, 10));
        top.add(title, BorderLayout.CENTER);

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(8, 10, 10, 10));
        body.add(top, BorderLayout.NORTH);
        body.add(valueLabel, BorderLayout.CENTER);

        card.add(body, BorderLayout.CENTER);
        card.putClientProperty("accent", accent);
        return card;
    }

    private JLabel metricValue() {
        JLabel label = new JLabel("0");
        label.setFont(UITheme.HEADER_FONT.deriveFont(Font.BOLD, 34f));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private JButton styledButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), UITheme.BUTTON_RADIUS, UITheme.BUTTON_RADIUS);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(UITheme.BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        return button;
    }

    private void loadReport() {
        try {
            totalPatientsValue.setText(countFormat.format(new PatientController().totalPatients()));
            activeDoctorsValue.setText(countFormat.format(new DoctorController().totalActive()));
            unpaidBillsValue.setText(countFormat.format(countInt("SELECT COUNT(*) FROM billing WHERE payment_status='Unpaid'", null)));

            YearMonth currentMonth = YearMonth.now();
            LocalDate monthStart = currentMonth.atDay(1);
            LocalDate nextMonthStart = currentMonth.plusMonths(1).atDay(1);

            monthlyAppointmentsValue.setText(countFormat.format(countInt(
                    "SELECT COUNT(*) FROM appointments WHERE appointment_date >= ? AND appointment_date < ?",
                    ps -> {
                        ps.setDate(1, Date.valueOf(monthStart));
                        ps.setDate(2, Date.valueOf(nextMonthStart));
                    }
            )));

            completedAppointmentsValue.setText(countFormat.format(countInt(
                    "SELECT COUNT(*) FROM appointments WHERE status='Completed'",
                    null
            )));

            BigDecimal monthlyRevenue = sumDecimal(
                    "SELECT COALESCE(SUM(total_amount), 0) FROM billing WHERE payment_status='Paid' AND bill_date >= ? AND bill_date < ?",
                    ps -> {
                        ps.setDate(1, Date.valueOf(monthStart));
                        ps.setDate(2, Date.valueOf(nextMonthStart));
                    }
            );
            monthlyRevenueValue.setText("Rs. " + moneyFormat.format(monthlyRevenue));

            List<String> monthLabels = new ArrayList<>();
            List<Double> appointmentSeries = new ArrayList<>();
            List<Double> revenueSeries = new ArrayList<>();
            for (int offset = 5; offset >= 0; offset--) {
                YearMonth month = currentMonth.minusMonths(offset);
                LocalDate start = month.atDay(1);
                LocalDate end = month.plusMonths(1).atDay(1);
                monthLabels.add(month.format(monthFormatter));
                appointmentSeries.add((double) countInt(
                        "SELECT COUNT(*) FROM appointments WHERE appointment_date >= ? AND appointment_date < ?",
                        ps -> {
                            ps.setDate(1, Date.valueOf(start));
                            ps.setDate(2, Date.valueOf(end));
                        }
                ));
                revenueSeries.add(sumDecimal(
                        "SELECT COALESCE(SUM(total_amount), 0) FROM billing WHERE payment_status='Paid' AND bill_date >= ? AND bill_date < ?",
                        ps -> {
                            ps.setDate(1, Date.valueOf(start));
                            ps.setDate(2, Date.valueOf(end));
                        }
                ).doubleValue());
            }

            appointmentsChart.setData(monthLabels, appointmentSeries);
            revenueChart.setData(monthLabels, revenueSeries);
                appointmentStatusChart.setData(queryGroupedCounts("SELECT ISNULL(status, 'Unknown') AS label, COUNT(*) AS total FROM appointments GROUP BY status ORDER BY total DESC, label ASC"));
                billingStatusChart.setData(queryGroupedCounts("SELECT ISNULL(payment_status, 'Unknown') AS label, COUNT(*) AS total FROM billing GROUP BY payment_status ORDER BY total DESC, label ASC"));

            loadRecentActivity();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Reports", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void printReport() {
        try {
            reportContent.revalidate();
            reportContent.doLayout();
            reportContent.setSize(reportContent.getPreferredSize());

            PrinterJob job = PrinterJob.getPrinterJob();
            job.setJobName("Hospital Admin Report");
            job.setPrintable(new Printable() {
                @Override
                public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                    reportContent.revalidate();
                    reportContent.doLayout();

                    int headerHeight = 52;
                    double imageableWidth = pageFormat.getImageableWidth();
                    double imageableHeight = pageFormat.getImageableHeight() - headerHeight;
                    if (imageableWidth <= 0 || imageableHeight <= 0) {
                        return NO_SUCH_PAGE;
                    }

                    Dimension preferredSize = reportContent.getPreferredSize();
                    if (preferredSize.width <= 0 || preferredSize.height <= 0) {
                        return NO_SUCH_PAGE;
                    }

                    double scale = Math.min(1d, imageableWidth / preferredSize.width);
                    double scaledContentHeight = preferredSize.height * scale;
                    int totalPages = Math.max(1, (int) Math.ceil(scaledContentHeight / imageableHeight));
                    if (pageIndex >= totalPages) {
                        return NO_SUCH_PAGE;
                    }

                    Graphics2D g2 = (Graphics2D) graphics.create();
                    g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    g2.setColor(Color.WHITE);
                    g2.fillRect(0, 0, (int) pageFormat.getImageableWidth(), (int) pageFormat.getImageableHeight());

                    g2.setFont(UITheme.HEADER_FONT.deriveFont(Font.BOLD, 18f));
                    g2.setColor(UITheme.PRIMARY_DARK);
                    g2.drawString("Admin Reports", 0, 20);
                    g2.setFont(UITheme.BODY_FONT.deriveFont(Font.PLAIN, 11f));
                    g2.setColor(new Color(85, 95, 110));
                    g2.drawString("Generated for " + user.getFullName() + " on " + LocalDate.now(), 0, 38);

                    Graphics2D contentGraphics = (Graphics2D) g2.create();
                    contentGraphics.translate(0, headerHeight);
                    contentGraphics.scale(scale, scale);
                    contentGraphics.translate(0, -(pageIndex * imageableHeight) / scale);
                    reportContent.printAll(contentGraphics);
                    contentGraphics.dispose();
                    g2.dispose();
                    return PAGE_EXISTS;
                }
            });

            if (!job.printDialog()) {
                return;
            }
            job.print();
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Print Report", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadRecentActivity() throws SQLException {
        activityModel.setRowCount(0);
        String sql = "SELECT TOP 10 table_name, record_id, changed_by, change_description, change_timestamp FROM audit_log ORDER BY change_timestamp DESC, log_id DESC";
        try (Connection connection = DBConnection.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Timestamp timestamp = rs.getTimestamp("change_timestamp");
                activityModel.addRow(new Object[]{
                        timestamp == null ? "" : activityFormatter.format(timestamp.toLocalDateTime()),
                        safeText(rs.getString("table_name")),
                        safeText(rs.getString("record_id")),
                        safeText(rs.getString("changed_by")),
                        safeText(rs.getString("change_description"))
                });
            }
        }
    }

    private String safeText(String text) {
        return text == null || text.isBlank() ? "-" : text;
    }

    private int countInt(String sql, StatementConfigurer configurer) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            if (configurer != null) {
                configurer.accept(ps);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private BigDecimal sumDecimal(String sql, StatementConfigurer configurer) throws SQLException {
        try (Connection connection = DBConnection.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            if (configurer != null) {
                configurer.accept(ps);
            }
            try (ResultSet rs = ps.executeQuery()) {
                BigDecimal value = rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
                return value == null ? BigDecimal.ZERO : value;
            }
        }
    }

    private LinkedHashMap<String, Double> queryGroupedCounts(String sql) throws SQLException {
        LinkedHashMap<String, Double> values = new LinkedHashMap<>();
        try (Connection connection = DBConnection.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                values.put(safeText(rs.getString(1)), rs.getDouble(2));
            }
        }
        return values;
    }

    @FunctionalInterface
    private interface StatementConfigurer {
        void accept(PreparedStatement statement) throws SQLException;
    }

    private static final class RoundedCard extends JPanel {
        private RoundedCard() {
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
            Object accentValue = getClientProperty("accent");
            if (accentValue instanceof Color accent) {
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, getWidth(), 52, 22, 22);
            }
            g2.setColor(new Color(0, 0, 0, 22));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static final class BarChartPanel extends JPanel {
        private final String title;
        private final String subtitle;
        private final Color barColor;
        private final boolean currency;
        private final DecimalFormat countFormat = new DecimalFormat("#,##0");
        private final DecimalFormat moneyFormat = new DecimalFormat("#,##0.00");
        private List<String> labels = List.of();
        private List<Double> values = List.of();

        private BarChartPanel(String title, String subtitle, Color barColor, boolean currency) {
            this.title = title;
            this.subtitle = subtitle;
            this.barColor = barColor;
            this.currency = currency;
            setPreferredSize(new Dimension(0, 280));
            setOpaque(false);
        }

        private void setData(List<String> labels, List<Double> values) {
            this.labels = new ArrayList<>(labels);
            this.values = new ArrayList<>(values);
            repaint();
        }

        private void setData(LinkedHashMap<String, Double> valuesByLabel) {
            this.labels = new ArrayList<>(valuesByLabel.keySet());
            this.values = new ArrayList<>(valuesByLabel.values());
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
            g2.setColor(new Color(0, 0, 0, 22));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);

            int left = 18;
            int top = 18;
            int right = 18;
            int bottom = 30;

            Font titleFont = UITheme.BUTTON_FONT.deriveFont(Font.BOLD, 14f);
            Font subtitleFont = UITheme.BODY_FONT.deriveFont(Font.PLAIN, 12f);
            g2.setFont(titleFont);
            g2.setColor(UITheme.PRIMARY_DARK);
            g2.drawString(title, left, top + 12);

            g2.setFont(subtitleFont);
            g2.setColor(new Color(100, 110, 125));
            g2.drawString(subtitle, left, top + 32);

            if (values.isEmpty()) {
                g2.setColor(new Color(120, 120, 120));
                g2.setFont(UITheme.BODY_FONT.deriveFont(Font.PLAIN, 13f));
                g2.drawString("No data available", left, getHeight() / 2);
                g2.dispose();
                super.paintComponent(g);
                return;
            }

            int chartTop = 62;
            int chartBottom = getHeight() - bottom;
            int chartLeft = left + 6;
            int chartRight = getWidth() - right - 8;
            int chartHeight = Math.max(1, chartBottom - chartTop);
            int chartWidth = Math.max(1, chartRight - chartLeft);

            double max = values.stream().mapToDouble(Double::doubleValue).max().orElse(1d);
            if (max <= 0d) {
                max = 1d;
            }

            int count = values.size();
            int slotWidth = Math.max(1, chartWidth / count);
            int barWidth = Math.max(16, (int) (slotWidth * 0.62));

            g2.setColor(new Color(230, 236, 242));
            g2.drawLine(chartLeft, chartBottom, chartRight, chartBottom);

            Font labelFont = UITheme.BODY_FONT.deriveFont(Font.PLAIN, 11f);
            FontMetrics labelMetrics = g2.getFontMetrics(labelFont);
            FontMetrics valueMetrics = g2.getFontMetrics(UITheme.BODY_FONT.deriveFont(Font.BOLD, 11f));
            g2.setFont(UITheme.BODY_FONT.deriveFont(Font.BOLD, 11f));

            for (int i = 0; i < count; i++) {
                double value = values.get(i) == null ? 0d : values.get(i);
                int barHeight = (int) Math.round((value / max) * (chartHeight - 28));
                int x = chartLeft + (i * slotWidth) + (slotWidth - barWidth) / 2;
                int y = chartBottom - barHeight;

                GradientPaint barPaint = new GradientPaint(x, y, barColor.brighter(), x, chartBottom, barColor.darker());
                g2.setPaint(barPaint);
                g2.fillRoundRect(x, y, barWidth, barHeight, 12, 12);

                g2.setColor(new Color(70, 80, 95));
                String valueText = currency ? "Rs. " + moneyFormat.format(value) : countFormat.format(value);
                int valueWidth = valueMetrics.stringWidth(valueText);
                g2.drawString(valueText, x + (barWidth - valueWidth) / 2, Math.max(chartTop + 12, y - 6));

                g2.setFont(labelFont);
                String label = labels.get(i);
                String shortLabel = shorten(label, 14);
                int labelWidth = labelMetrics.stringWidth(shortLabel);
                g2.drawString(shortLabel, x + (barWidth - labelWidth) / 2, chartBottom + 16);
                g2.setFont(UITheme.BODY_FONT.deriveFont(Font.BOLD, 11f));
            }

            g2.dispose();
            super.paintComponent(g);
        }

        private String shorten(String text, int maxLength) {
            if (text == null) {
                return "-";
            }
            if (text.length() <= maxLength) {
                return text;
            }
            return text.substring(0, Math.max(0, maxLength - 3)) + "...";
        }
    }
}