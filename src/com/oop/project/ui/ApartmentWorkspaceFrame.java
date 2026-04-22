package com.oop.project.ui;

import com.oop.project.db.DBConnection;
import com.oop.project.model.User;
import com.oop.project.service.LoginAuthentication;
import com.oop.project.service.RegisterAuthentication;
import com.oop.project.ui.components.GradientPanel;
import com.oop.project.ui.components.RoundedPanel;
import com.oop.project.ui.model.ApartmentRow;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

public class ApartmentWorkspaceFrame extends JFrame {

	private static final Color NAVY = new Color(22, 40, 74);
	private static final Color CYAN = new Color(26, 188, 156);
	private static final Color SOFT_BG = new Color(244, 248, 252);
	private static final Color CARD_BG = new Color(255, 255, 255);
	private static final Color MUTED_TEXT = new Color(90, 98, 116);
	private static final Color DANGER = new Color(198, 40, 40);
	private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 30);
	private static final Font FONT_H2 = new Font("Segoe UI", Font.BOLD, 20);
	private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
	private static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 14);
	private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("#,##0.00");

	private static final String CARD_LISTINGS = "LISTINGS";
	private static final String CARD_FILTERS = "FILTERS";
	private static final String CARD_FAVORITES = "FAVORITES";
	private static final String CARD_NOTES = "NOTES";
	private static final String CARD_DASHBOARD = "DASHBOARD";

	private User currentUser;
	private List<ApartmentRow> apartmentRows = new ArrayList<>();

	private CardLayout contentCardLayout;
	private JPanel contentCards;
	private JLabel statusLabel;

	private DefaultTableModel listingsModel;
	private DefaultTableModel filtersModel;
	private DefaultTableModel favoritesModel;
	private DefaultTableModel notesModel;

	private JTable listingsTable;
	private JTable filtersTable;
	private JTable favoritesTable;
	private JTable notesTable;

	private JTextField searchField;
	private JComboBox<String> sortCombo;

	private JSpinner maxPriceSpinner;
	private JSpinner minBedroomsSpinner;
	private JComboBox<String> cityFilterCombo;
	private JComboBox<String> categoryFilterCombo;
	private JComboBox<String> amenityFilterCombo;

	private JLabel metricTotalListings;
	private JLabel metricAveragePrice;
	private JLabel metricFavorites;
	private JLabel metricTopCity;
	private JTextArea metricCategoryBreakdown;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			ApartmentWorkspaceFrame app = new ApartmentWorkspaceFrame();
			app.setVisible(true);
		});
	}

	public ApartmentWorkspaceFrame() {
		setTitle("Real Estate Apartment Platform");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1360, 820);
		setMinimumSize(new Dimension(1200, 760));
		setLocationRelativeTo(null);
		setContentPane(buildAuthView());
	}

	private JPanel buildAuthView() {
		GradientPanel root = new GradientPanel(new Color(15, 33, 59), new Color(48, 93, 132));
		root.setLayout(new GridBagLayout());

		RoundedPanel authCard = new RoundedPanel(28, CARD_BG);
		authCard.setPreferredSize(new Dimension(500, 540));
		authCard.setLayout(new BorderLayout(0, 18));
		authCard.setBorder(new EmptyBorder(30, 34, 30, 34));

		JLabel appName = new JLabel("Astera Residence Console", SwingConstants.CENTER);
		appName.setFont(FONT_TITLE);
		appName.setForeground(NAVY);

		JLabel subtitle = new JLabel("Apartment Listing, Filter, Favorites, Notes", SwingConstants.CENTER);
		subtitle.setFont(FONT_BODY);
		subtitle.setForeground(MUTED_TEXT);

		JPanel titleWrap = new JPanel();
		titleWrap.setOpaque(false);
		titleWrap.setLayout(new BoxLayout(titleWrap, BoxLayout.Y_AXIS));
		appName.setAlignmentX(Component.CENTER_ALIGNMENT);
		subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		titleWrap.add(appName);
		titleWrap.add(Box.createVerticalStrut(6));
		titleWrap.add(subtitle);

		JTextField usernameField = createTextField("Username");
		JPasswordField passwordField = new JPasswordField();
		styleInput(passwordField);
		JComboBox<String> roleCombo = new JComboBox<>(new String[] {"admin", "agent"});
		styleCombo(roleCombo);

		JPanel form = new JPanel(new GridBagLayout());
		form.setOpaque(false);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(8, 0, 8, 0);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;

		form.add(labelFor("Username"), gbc);
		gbc.gridy++;
		form.add(usernameField, gbc);
		gbc.gridy++;
		form.add(labelFor("Password"), gbc);
		gbc.gridy++;
		form.add(passwordField, gbc);
		gbc.gridy++;
		form.add(labelFor("Role"), gbc);
		gbc.gridy++;
		form.add(roleCombo, gbc);

		JButton loginButton = createActionButton("Login", NAVY, Color.WHITE);
		JButton registerButton = createActionButton("Register", CYAN, Color.WHITE);

		loginButton.addActionListener(event -> {
			String username = usernameField.getText().trim();
			String password = String.valueOf(passwordField.getPassword());
			String role = String.valueOf(roleCombo.getSelectedItem());
			handleLogin(username, password, role);
		});

		registerButton.addActionListener(event -> {
			String username = usernameField.getText().trim();
			String password = String.valueOf(passwordField.getPassword());
			String role = String.valueOf(roleCombo.getSelectedItem());
			handleRegister(username, password, role);
		});

		JPanel buttons = new JPanel(new GridLayout(1, 2, 10, 0));
		buttons.setOpaque(false);
		buttons.add(loginButton);
		buttons.add(registerButton);

		JLabel hint = new JLabel(
				"Tip: UI uses the existing backend services for login/register.",
				SwingConstants.CENTER);
		hint.setFont(new Font("Segoe UI", Font.ITALIC, 13));
		hint.setForeground(MUTED_TEXT);

		JPanel center = new JPanel(new BorderLayout(0, 16));
		center.setOpaque(false);
		center.add(form, BorderLayout.CENTER);
		center.add(buttons, BorderLayout.SOUTH);

		authCard.add(titleWrap, BorderLayout.NORTH);
		authCard.add(center, BorderLayout.CENTER);
		authCard.add(hint, BorderLayout.SOUTH);

		root.add(authCard);
		return root;
	}

	private void handleLogin(String username, String password, String role) {
		if (username.isBlank() || password.isBlank()) {
			JOptionPane.showMessageDialog(this, "Username and password are required.", "Validation", JOptionPane.WARNING_MESSAGE);
			return;
		}

		try {
			User user = LoginAuthentication.getUser(username, password, role);
			currentUser = user;
			buildMainView();
			setStatus("Welcome back, " + currentUser.getUsername() + "!");
		} catch (Exception e) {
			showError("Login failed", e);
		}
	}

	private void handleRegister(String username, String password, String role) {
		if (username.isBlank() || password.isBlank()) {
			JOptionPane.showMessageDialog(this, "Username and password are required.", "Validation", JOptionPane.WARNING_MESSAGE);
			return;
		}

		try {
			RegisterAuthentication.createUser(username, password, role);
			JOptionPane.showMessageDialog(this, "Register success. You can login now.", "Success", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			showError("Register failed", e);
		}
	}

	private void buildMainView() {
		JPanel root = new JPanel(new BorderLayout());
		root.setBackground(SOFT_BG);

		JPanel sidebar = buildSidebar();
		JPanel topBar = buildTopBar();

		listingsModel = createReadOnlyModel(
				new String[] {"ID", "Address", "City", "Price (B VND)", "Bedrooms", "Size (sqm)", "Category", "Amenities"},
				new Class<?>[] {Integer.class, String.class, String.class, Double.class, Integer.class, Double.class, String.class, String.class});
		filtersModel = createReadOnlyModel(
				new String[] {"ID", "Address", "City", "Price (B VND)", "Bedrooms", "Size (sqm)", "Category", "Amenities"},
				new Class<?>[] {Integer.class, String.class, String.class, Double.class, Integer.class, Double.class, String.class, String.class});
		favoritesModel = createReadOnlyModel(
				new String[] {"ID", "Address", "City", "Price (B VND)", "Category", "Saved At"},
				new Class<?>[] {Integer.class, String.class, String.class, Double.class, String.class, Timestamp.class});
		notesModel = createReadOnlyModel(
				new String[] {"Note ID", "Apartment ID", "Content", "Created At", "Updated At"},
				new Class<?>[] {Integer.class, Integer.class, String.class, Timestamp.class, Timestamp.class});

		listingsTable = createStyledTable(listingsModel);
		filtersTable = createStyledTable(filtersModel);
		favoritesTable = createStyledTable(favoritesModel);
		notesTable = createStyledTable(notesModel);

		contentCardLayout = new CardLayout();
		contentCards = new JPanel(contentCardLayout);
		contentCards.setOpaque(false);
		contentCards.setBorder(new EmptyBorder(12, 12, 12, 12));

		contentCards.add(buildListingsPanel(), CARD_LISTINGS);
		contentCards.add(buildFiltersPanel(), CARD_FILTERS);
		contentCards.add(buildFavoritesPanel(), CARD_FAVORITES);
		contentCards.add(buildNotesPanel(), CARD_NOTES);
		contentCards.add(buildDashboardPanel(), CARD_DASHBOARD);

		JPanel center = new JPanel(new BorderLayout());
		center.setOpaque(false);
		center.add(topBar, BorderLayout.NORTH);
		center.add(contentCards, BorderLayout.CENTER);

		statusLabel = new JLabel("Ready");
		statusLabel.setFont(FONT_BODY);
		statusLabel.setForeground(MUTED_TEXT);
		JPanel statusWrap = new JPanel(new BorderLayout());
		statusWrap.setBackground(new Color(234, 240, 248));
		statusWrap.setBorder(new EmptyBorder(10, 16, 10, 16));
		statusWrap.add(statusLabel, BorderLayout.WEST);
		center.add(statusWrap, BorderLayout.SOUTH);

		root.add(sidebar, BorderLayout.WEST);
		root.add(center, BorderLayout.CENTER);

		setContentPane(root);
		revalidate();
		repaint();

		loadAllData();
		contentCardLayout.show(contentCards, CARD_LISTINGS);
	}

	private JPanel buildSidebar() {
		GradientPanel sidebar = new GradientPanel(new Color(18, 33, 58), new Color(33, 62, 104));
		sidebar.setPreferredSize(new Dimension(240, 0));
		sidebar.setLayout(new BorderLayout());
		sidebar.setBorder(new EmptyBorder(24, 16, 24, 16));

		JLabel logoTitle = new JLabel("ASTERA", SwingConstants.LEFT);
		logoTitle.setForeground(Color.WHITE);
		logoTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));

		JLabel logoSubtitle = new JLabel("Residence Suite", SwingConstants.LEFT);
		logoSubtitle.setForeground(new Color(196, 232, 255));
		logoSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));

		JPanel logoWrap = new JPanel();
		logoWrap.setOpaque(false);
		logoWrap.setLayout(new BoxLayout(logoWrap, BoxLayout.Y_AXIS));
		logoWrap.add(logoTitle);
		logoWrap.add(logoSubtitle);
		logoWrap.add(Box.createVerticalStrut(20));

		JPanel nav = new JPanel();
		nav.setOpaque(false);
		nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));

		nav.add(createNavButton("Listings", CARD_LISTINGS));
		nav.add(Box.createVerticalStrut(8));
		nav.add(createNavButton("Filters", CARD_FILTERS));
		nav.add(Box.createVerticalStrut(8));
		nav.add(createNavButton("Favorites", CARD_FAVORITES));
		nav.add(Box.createVerticalStrut(8));
		nav.add(createNavButton("Notes", CARD_NOTES));
		nav.add(Box.createVerticalStrut(8));
		nav.add(createNavButton("Dashboard", CARD_DASHBOARD));
		nav.add(Box.createVerticalStrut(22));

		JButton refreshBtn = createActionButton("Refresh Data", CYAN, Color.WHITE);
		refreshBtn.addActionListener(event -> loadAllData());

		JButton logoutBtn = createActionButton("Logout", DANGER, Color.WHITE);
		logoutBtn.addActionListener(event -> {
			currentUser = null;
			setContentPane(buildAuthView());
			revalidate();
			repaint();
		});

		nav.add(refreshBtn);
		nav.add(Box.createVerticalStrut(10));
		nav.add(logoutBtn);

		sidebar.add(logoWrap, BorderLayout.NORTH);
		sidebar.add(nav, BorderLayout.CENTER);
		return sidebar;
	}

	private JPanel buildTopBar() {
		JPanel topBar = new JPanel(new BorderLayout());
		topBar.setBackground(SOFT_BG);
		topBar.setBorder(new EmptyBorder(20, 20, 8, 20));

		JLabel heading = new JLabel("Apartment Listing and Filter Workspace");
		heading.setFont(FONT_H2);
		heading.setForeground(NAVY);

		String roleText = currentUser != null ? currentUser.getRole().toUpperCase() : "UNKNOWN";
		String userText = currentUser != null ? currentUser.getUsername() : "guest";
		JLabel badge = new JLabel("User: " + userText + " 窶｢ Role: " + roleText, SwingConstants.RIGHT);
		badge.setFont(FONT_BODY_BOLD);
		badge.setForeground(new Color(44, 62, 80));

		topBar.add(heading, BorderLayout.WEST);
		topBar.add(badge, BorderLayout.EAST);
		return topBar;
	}

	private JPanel buildListingsPanel() {
		RoundedPanel panel = new RoundedPanel(22, CARD_BG);
		panel.setLayout(new BorderLayout(0, 14));
		panel.setBorder(new EmptyBorder(18, 18, 18, 18));

		JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		controls.setOpaque(false);

		searchField = createTextField("Search by address/city/amenity");
		searchField.setPreferredSize(new Dimension(290, 40));

		sortCombo = new JComboBox<>(new String[] {
				"Sort: ID",
				"Price: Low to High",
				"Price: High to Low",
				"Bedrooms: High to Low",
				"Size: High to Low",
				"Address: A to Z"
		});
		styleCombo(sortCombo);
		sortCombo.setPreferredSize(new Dimension(220, 40));

		JButton addFavoriteBtn = createActionButton("Add Favorite", NAVY, Color.WHITE);
		JButton addNoteBtn = createActionButton("Add Note", CYAN, Color.WHITE);
		JButton exportBtn = createActionButton("Export CSV", new Color(0, 121, 107), Color.WHITE);

		addFavoriteBtn.addActionListener(event -> addFavoriteFromTable(listingsTable));
		addNoteBtn.addActionListener(event -> openCreateNoteDialogFromTable(listingsTable));
		exportBtn.addActionListener(event -> exportVisibleListingsToCSV());

		addDocumentListener(searchField, this::applyListingSearchAndSort);
		sortCombo.addActionListener(event -> applyListingSearchAndSort());

		controls.add(searchField);
		controls.add(sortCombo);
		controls.add(addFavoriteBtn);
		controls.add(addNoteBtn);
		controls.add(exportBtn);

		JScrollPane tableWrap = new JScrollPane(listingsTable);
		tableWrap.setBorder(BorderFactory.createLineBorder(new Color(229, 236, 246)));

		panel.add(controls, BorderLayout.NORTH);
		panel.add(tableWrap, BorderLayout.CENTER);
		return panel;
	}

	private JPanel buildFiltersPanel() {
		RoundedPanel panel = new RoundedPanel(22, CARD_BG);
		panel.setLayout(new BorderLayout(0, 14));
		panel.setBorder(new EmptyBorder(18, 18, 18, 18));

		maxPriceSpinner = new JSpinner(new SpinnerNumberModel(100.0, 0.0, 1000.0, 0.1));
		minBedroomsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
		cityFilterCombo = new JComboBox<>(new String[] {"Any"});
		categoryFilterCombo = new JComboBox<>(new String[] {"Any", "luxury", "standard", "budget"});
		amenityFilterCombo = new JComboBox<>(new String[] {"Any"});

		styleSpinner(maxPriceSpinner);
		styleSpinner(minBedroomsSpinner);
		styleCombo(cityFilterCombo);
		styleCombo(categoryFilterCombo);
		styleCombo(amenityFilterCombo);

		JPanel filters = new JPanel(new GridLayout(2, 5, 10, 10));
		filters.setOpaque(false);
		filters.add(labelFor("Max Price (B VND)"));
		filters.add(labelFor("Min Bedrooms"));
		filters.add(labelFor("City"));
		filters.add(labelFor("Category"));
		filters.add(labelFor("Amenity"));
		filters.add(maxPriceSpinner);
		filters.add(minBedroomsSpinner);
		filters.add(cityFilterCombo);
		filters.add(categoryFilterCombo);
		filters.add(amenityFilterCombo);

		JButton resetBtn = createActionButton("Reset Filters", NAVY, Color.WHITE);
		JButton favBtn = createActionButton("Add Favorite", CYAN, Color.WHITE);
		JButton noteBtn = createActionButton("Add Note", new Color(0, 121, 107), Color.WHITE);

		resetBtn.addActionListener(event -> resetFilters());
		favBtn.addActionListener(event -> addFavoriteFromTable(filtersTable));
		noteBtn.addActionListener(event -> openCreateNoteDialogFromTable(filtersTable));

		JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		btnRow.setOpaque(false);
		btnRow.add(resetBtn);
		btnRow.add(favBtn);
		btnRow.add(noteBtn);

		JScrollPane tableWrap = new JScrollPane(filtersTable);
		tableWrap.setBorder(BorderFactory.createLineBorder(new Color(229, 236, 246)));

		JPanel top = new JPanel(new BorderLayout(0, 12));
		top.setOpaque(false);
		top.add(filters, BorderLayout.CENTER);
		top.add(btnRow, BorderLayout.SOUTH);

		panel.add(top, BorderLayout.NORTH);
		panel.add(tableWrap, BorderLayout.CENTER);

		maxPriceSpinner.addChangeListener(event -> applyFiltersLive());
		minBedroomsSpinner.addChangeListener(event -> applyFiltersLive());
		cityFilterCombo.addActionListener(event -> applyFiltersLive());
		categoryFilterCombo.addActionListener(event -> applyFiltersLive());
		amenityFilterCombo.addActionListener(event -> applyFiltersLive());

		return panel;
	}

	private JPanel buildFavoritesPanel() {
		RoundedPanel panel = new RoundedPanel(22, CARD_BG);
		panel.setLayout(new BorderLayout(0, 14));
		panel.setBorder(new EmptyBorder(18, 18, 18, 18));

		JButton removeBtn = createActionButton("Remove Selected Favorite", DANGER, Color.WHITE);
		JButton refreshBtn = createActionButton("Refresh", NAVY, Color.WHITE);

		removeBtn.addActionListener(event -> removeSelectedFavorite());
		refreshBtn.addActionListener(event -> refreshFavoritesTable());

		JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		actions.setOpaque(false);
		actions.add(removeBtn);
		actions.add(refreshBtn);

		JScrollPane tableWrap = new JScrollPane(favoritesTable);
		tableWrap.setBorder(BorderFactory.createLineBorder(new Color(229, 236, 246)));

		panel.add(actions, BorderLayout.NORTH);
		panel.add(tableWrap, BorderLayout.CENTER);
		return panel;
	}

	private JPanel buildNotesPanel() {
		RoundedPanel panel = new RoundedPanel(22, CARD_BG);
		panel.setLayout(new BorderLayout(0, 14));
		panel.setBorder(new EmptyBorder(18, 18, 18, 18));

		JButton addBtn = createActionButton("Create Note", CYAN, Color.WHITE);
		JButton editBtn = createActionButton("Edit Selected", NAVY, Color.WHITE);
		JButton deleteBtn = createActionButton("Delete Selected", DANGER, Color.WHITE);
		JButton refreshBtn = createActionButton("Refresh", new Color(0, 121, 107), Color.WHITE);

		addBtn.addActionListener(event -> openCreateNoteDialog(-1));
		editBtn.addActionListener(event -> updateSelectedNote());
		deleteBtn.addActionListener(event -> deleteSelectedNote());
		refreshBtn.addActionListener(event -> refreshNotesTable());

		JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		actions.setOpaque(false);
		actions.add(addBtn);
		actions.add(editBtn);
		actions.add(deleteBtn);
		actions.add(refreshBtn);

		JScrollPane tableWrap = new JScrollPane(notesTable);
		tableWrap.setBorder(BorderFactory.createLineBorder(new Color(229, 236, 246)));

		panel.add(actions, BorderLayout.NORTH);
		panel.add(tableWrap, BorderLayout.CENTER);
		return panel;
	}

	private JPanel buildDashboardPanel() {
		JPanel wrapper = new JPanel(new BorderLayout(0, 12));
		wrapper.setOpaque(false);

		JPanel metricsGrid = new JPanel(new GridLayout(1, 4, 12, 12));
		metricsGrid.setOpaque(false);

		metricTotalListings = createMetricCard(metricsGrid, "Total Listings", "0", new Color(15, 76, 117));
		metricAveragePrice = createMetricCard(metricsGrid, "Average Price", "0", new Color(39, 174, 96));
		metricFavorites = createMetricCard(metricsGrid, "My Favorites", "0", new Color(211, 84, 0));
		metricTopCity = createMetricCard(metricsGrid, "Top City", "-", new Color(123, 31, 162));

		RoundedPanel breakdownCard = new RoundedPanel(22, CARD_BG);
		breakdownCard.setLayout(new BorderLayout(0, 10));
		breakdownCard.setBorder(new EmptyBorder(16, 16, 16, 16));

		JLabel title = new JLabel("Category Distribution");
		title.setFont(FONT_H2);
		title.setForeground(NAVY);

		metricCategoryBreakdown = new JTextArea();
		metricCategoryBreakdown.setEditable(false);
		metricCategoryBreakdown.setFont(new Font("Consolas", Font.PLAIN, 14));
		metricCategoryBreakdown.setForeground(new Color(44, 62, 80));
		metricCategoryBreakdown.setBackground(CARD_BG);

		breakdownCard.add(title, BorderLayout.NORTH);
		breakdownCard.add(metricCategoryBreakdown, BorderLayout.CENTER);

		wrapper.add(metricsGrid, BorderLayout.NORTH);
		wrapper.add(breakdownCard, BorderLayout.CENTER);
		return wrapper;
	}

	private JLabel createMetricCard(JPanel holder, String titleText, String valueText, Color accent) {
		RoundedPanel card = new RoundedPanel(22, CARD_BG);
		card.setLayout(new BorderLayout(0, 8));
		card.setBorder(new EmptyBorder(14, 14, 14, 14));

		JLabel title = new JLabel(titleText);
		title.setFont(FONT_BODY_BOLD);
		title.setForeground(new Color(86, 99, 120));

		JLabel value = new JLabel(valueText);
		value.setFont(new Font("Segoe UI", Font.BOLD, 28));
		value.setForeground(accent);

		card.add(title, BorderLayout.NORTH);
		card.add(value, BorderLayout.CENTER);
		holder.add(card);
		return value;
	}

	private void loadAllData() {
		try {
			apartmentRows = fetchApartments();
			refreshListingsTable();
			refreshFilterOptions();
			applyFiltersLive();
			refreshFavoritesTable();
			refreshNotesTable();
			refreshDashboard();
			setStatus("Data refreshed: " + apartmentRows.size() + " apartments loaded.");
		} catch (SQLException e) {
			showError("Cannot load apartments from database", e);
			setStatus("Data refresh failed.");
		}
	}

	private void refreshListingsTable() {
		listingsModel.setRowCount(0);
		for (ApartmentRow row : apartmentRows) {
			listingsModel.addRow(toApartmentTableRow(row));
		}
		applyListingSearchAndSort();
	}

	private void refreshFilterOptions() {
		TreeSet<String> cities = new TreeSet<>();
		double maxPrice = 0.0;

		for (ApartmentRow row : apartmentRows) {
			cities.add(row.city);
			if (row.price > maxPrice) {
				maxPrice = row.price;
			}
		}

		DefaultComboBoxModel<String> cityModel = new DefaultComboBoxModel<>();
		cityModel.addElement("Any");
		for (String city : cities) {
			cityModel.addElement(city);
		}
		cityFilterCombo.setModel(cityModel);
		styleCombo(cityFilterCombo);

		DefaultComboBoxModel<String> amenityModel = new DefaultComboBoxModel<>();
		amenityModel.addElement("Any");
		for (String amenity : fetchAllAmenities()) {
			amenityModel.addElement(amenity);
		}
		amenityFilterCombo.setModel(amenityModel);
		styleCombo(amenityFilterCombo);

		double spinnerMax = Math.max(10.0, maxPrice + 5.0);
		maxPriceSpinner.setModel(new SpinnerNumberModel(spinnerMax, 0.0, spinnerMax, 0.1));
		minBedroomsSpinner.setModel(new SpinnerNumberModel(1, 1, 20, 1));
	}

	private void applyListingSearchAndSort() {
		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(listingsModel);
		listingsTable.setRowSorter(sorter);

		String keyword = searchField != null ? searchField.getText().trim() : "";
		if (!keyword.isEmpty()) {
			sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(keyword), 1, 2, 7));
		}

		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		String choice = sortCombo != null ? String.valueOf(sortCombo.getSelectedItem()) : "Sort: ID";
		if ("Price: Low to High".equals(choice)) {
			sortKeys.add(new RowSorter.SortKey(3, javax.swing.SortOrder.ASCENDING));
		} else if ("Price: High to Low".equals(choice)) {
			sortKeys.add(new RowSorter.SortKey(3, javax.swing.SortOrder.DESCENDING));
		} else if ("Bedrooms: High to Low".equals(choice)) {
			sortKeys.add(new RowSorter.SortKey(4, javax.swing.SortOrder.DESCENDING));
		} else if ("Size: High to Low".equals(choice)) {
			sortKeys.add(new RowSorter.SortKey(5, javax.swing.SortOrder.DESCENDING));
		} else if ("Address: A to Z".equals(choice)) {
			sortKeys.add(new RowSorter.SortKey(1, javax.swing.SortOrder.ASCENDING));
		} else {
			sortKeys.add(new RowSorter.SortKey(0, javax.swing.SortOrder.ASCENDING));
		}
		sorter.setSortKeys(sortKeys);
		sorter.sort();
	}

	private void applyFiltersLive() {
		if (maxPriceSpinner == null || minBedroomsSpinner == null) {
			return;
		}

		double maxPrice = ((Number) maxPriceSpinner.getValue()).doubleValue();
		int minBedrooms = ((Number) minBedroomsSpinner.getValue()).intValue();
		String city = String.valueOf(cityFilterCombo.getSelectedItem());
		String category = String.valueOf(categoryFilterCombo.getSelectedItem());
		String amenity = String.valueOf(amenityFilterCombo.getSelectedItem());

		filtersModel.setRowCount(0);
		for (ApartmentRow row : apartmentRows) {
			if (row.price > maxPrice) {
				continue;
			}
			if (row.bedrooms < minBedrooms) {
				continue;
			}
			if (!"Any".equals(city) && !row.city.equalsIgnoreCase(city)) {
				continue;
			}
			if (!"Any".equals(category) && !row.category.equalsIgnoreCase(category)) {
				continue;
			}
			if (!"Any".equals(amenity) && !row.amenities.toLowerCase().contains(amenity.toLowerCase())) {
				continue;
			}
			filtersModel.addRow(toApartmentTableRow(row));
		}

		setStatus("Filter matched " + filtersModel.getRowCount() + " apartment(s).");
	}

	private void resetFilters() {
		maxPriceSpinner.setValue(((SpinnerNumberModel) maxPriceSpinner.getModel()).getMaximum());
		minBedroomsSpinner.setValue(1);
		cityFilterCombo.setSelectedItem("Any");
		categoryFilterCombo.setSelectedItem("Any");
		amenityFilterCombo.setSelectedItem("Any");
		applyFiltersLive();
	}

	private void addFavoriteFromTable(JTable table) {
		int selectedViewRow = table.getSelectedRow();
		if (selectedViewRow < 0) {
			JOptionPane.showMessageDialog(this, "Select an apartment first.", "Info", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		int selectedModelRow = table.convertRowIndexToModel(selectedViewRow);
		int apartmentId = (Integer) table.getModel().getValueAt(selectedModelRow, 0);

		String sql = "INSERT INTO favourites(username, apartment_id) VALUES(?, ?)";
		try (Connection con = DBConnection.establish();
			 PreparedStatement statement = con.prepareStatement(sql)) {
			statement.setString(1, currentUser.getUsername());
			statement.setInt(2, apartmentId);
			statement.executeUpdate();
			refreshFavoritesTable();
			refreshDashboard();
			setStatus("Apartment #" + apartmentId + " added to favorites.");
		} catch (SQLException e) {
			showError("Cannot add favorite (it may already exist)", e);
		}
	}

	private void removeSelectedFavorite() {
		int selectedViewRow = favoritesTable.getSelectedRow();
		if (selectedViewRow < 0) {
			JOptionPane.showMessageDialog(this, "Select a favorite to remove.", "Info", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		int selectedModelRow = favoritesTable.convertRowIndexToModel(selectedViewRow);
		int apartmentId = (Integer) favoritesModel.getValueAt(selectedModelRow, 0);

		String sql = "DELETE FROM favourites WHERE username = ? AND apartment_id = ?";
		try (Connection con = DBConnection.establish();
			 PreparedStatement statement = con.prepareStatement(sql)) {
			statement.setString(1, currentUser.getUsername());
			statement.setInt(2, apartmentId);
			int affected = statement.executeUpdate();
			if (affected > 0) {
				refreshFavoritesTable();
				refreshDashboard();
				setStatus("Removed apartment #" + apartmentId + " from favorites.");
			}
		} catch (SQLException e) {
			showError("Cannot remove favorite", e);
		}
	}

	private void openCreateNoteDialogFromTable(JTable table) {
		int selectedViewRow = table.getSelectedRow();
		int apartmentId = -1;
		if (selectedViewRow >= 0) {
			int selectedModelRow = table.convertRowIndexToModel(selectedViewRow);
			apartmentId = (Integer) table.getModel().getValueAt(selectedModelRow, 0);
		}
		openCreateNoteDialog(apartmentId);
	}

	private void openCreateNoteDialog(int defaultApartmentId) {
		if (apartmentRows.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No apartment available.", "Info", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		JComboBox<Integer> apartmentCombo = new JComboBox<>();
		for (ApartmentRow row : apartmentRows) {
			apartmentCombo.addItem(row.id);
		}
		styleCombo(apartmentCombo);
		if (defaultApartmentId > 0) {
			apartmentCombo.setSelectedItem(defaultApartmentId);
		}

		JTextArea noteArea = new JTextArea(6, 28);
		noteArea.setLineWrap(true);
		noteArea.setWrapStyleWord(true);
		noteArea.setFont(FONT_BODY);
		JScrollPane noteScroll = new JScrollPane(noteArea);
		noteScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 228, 238)));

		JPanel panel = new JPanel(new BorderLayout(0, 8));
		panel.add(labelFor("Apartment ID"), BorderLayout.NORTH);

		JPanel center = new JPanel(new BorderLayout(0, 8));
		center.add(apartmentCombo, BorderLayout.NORTH);
		center.add(labelFor("Note Content"), BorderLayout.CENTER);

		JPanel wrap = new JPanel(new BorderLayout(0, 8));
		wrap.add(center, BorderLayout.NORTH);
		wrap.add(noteScroll, BorderLayout.CENTER);

		panel.add(wrap, BorderLayout.CENTER);

		int result = JOptionPane.showConfirmDialog(this, panel, "Create Note", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result != JOptionPane.OK_OPTION) {
			return;
		}

		String content = noteArea.getText().trim();
		if (content.isBlank()) {
			JOptionPane.showMessageDialog(this, "Note content cannot be empty.", "Validation", JOptionPane.WARNING_MESSAGE);
			return;
		}

		int apartmentId = (Integer) apartmentCombo.getSelectedItem();
		String sql = "INSERT INTO notes(username, apartment_id, content) VALUES(?, ?, ?)";
		try (Connection con = DBConnection.establish();
			 PreparedStatement statement = con.prepareStatement(sql)) {
			statement.setString(1, currentUser.getUsername());
			statement.setInt(2, apartmentId);
			statement.setString(3, content);
			statement.executeUpdate();
			refreshNotesTable();
			setStatus("Note created for apartment #" + apartmentId + ".");
		} catch (SQLException e) {
			showError("Cannot create note", e);
		}
	}

	private void updateSelectedNote() {
		int selectedViewRow = notesTable.getSelectedRow();
		if (selectedViewRow < 0) {
			JOptionPane.showMessageDialog(this, "Select a note to edit.", "Info", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		int selectedModelRow = notesTable.convertRowIndexToModel(selectedViewRow);
		int noteId = (Integer) notesModel.getValueAt(selectedModelRow, 0);
		String currentContent = String.valueOf(notesModel.getValueAt(selectedModelRow, 2));

		JTextArea noteArea = new JTextArea(currentContent, 7, 28);
		noteArea.setLineWrap(true);
		noteArea.setWrapStyleWord(true);
		noteArea.setFont(FONT_BODY);

		int result = JOptionPane.showConfirmDialog(
				this,
				new JScrollPane(noteArea),
				"Edit Note #" + noteId,
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		if (result != JOptionPane.OK_OPTION) {
			return;
		}

		String newContent = noteArea.getText().trim();
		if (newContent.isBlank()) {
			JOptionPane.showMessageDialog(this, "Note content cannot be empty.", "Validation", JOptionPane.WARNING_MESSAGE);
			return;
		}

		String sql = "UPDATE notes SET content = ?, updated_at = NOW() WHERE note_id = ? AND username = ?";
		try (Connection con = DBConnection.establish();
			 PreparedStatement statement = con.prepareStatement(sql)) {
			statement.setString(1, newContent);
			statement.setInt(2, noteId);
			statement.setString(3, currentUser.getUsername());
			int affected = statement.executeUpdate();
			if (affected > 0) {
				refreshNotesTable();
				setStatus("Note #" + noteId + " updated.");
			}
		} catch (SQLException e) {
			showError("Cannot update note", e);
		}
	}

	private void deleteSelectedNote() {
		int selectedViewRow = notesTable.getSelectedRow();
		if (selectedViewRow < 0) {
			JOptionPane.showMessageDialog(this, "Select a note to delete.", "Info", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		int selectedModelRow = notesTable.convertRowIndexToModel(selectedViewRow);
		int noteId = (Integer) notesModel.getValueAt(selectedModelRow, 0);

		int confirm = JOptionPane.showConfirmDialog(
				this,
				"Delete note #" + noteId + "?",
				"Confirm",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE);
		if (confirm != JOptionPane.YES_OPTION) {
			return;
		}

		String sql = "DELETE FROM notes WHERE note_id = ? AND username = ?";
		try (Connection con = DBConnection.establish();
			 PreparedStatement statement = con.prepareStatement(sql)) {
			statement.setInt(1, noteId);
			statement.setString(2, currentUser.getUsername());
			int affected = statement.executeUpdate();
			if (affected > 0) {
				refreshNotesTable();
				setStatus("Note #" + noteId + " deleted.");
			}
		} catch (SQLException e) {
			showError("Cannot delete note", e);
		}
	}

	private void refreshFavoritesTable() {
		favoritesModel.setRowCount(0);
		String sql = """
				SELECT a.apartment_id, a.address, a.city, a.price, a.category, f.created_at
				FROM favourites f
				JOIN apartment a ON a.apartment_id = f.apartment_id
				WHERE f.username = ?
				ORDER BY f.created_at DESC
				""";

		try (Connection con = DBConnection.establish();
			 PreparedStatement statement = con.prepareStatement(sql)) {
			statement.setString(1, currentUser.getUsername());
			try (ResultSet rs = statement.executeQuery()) {
				while (rs.next()) {
					favoritesModel.addRow(new Object[] {
							rs.getInt("apartment_id"),
							rs.getString("address"),
							rs.getString("city"),
							rs.getDouble("price"),
							rs.getString("category"),
							rs.getTimestamp("created_at")
					});
				}
			}
		} catch (SQLException e) {
			showError("Cannot load favorites", e);
		}
	}

	private void refreshNotesTable() {
		notesModel.setRowCount(0);
		String sql = """
				SELECT note_id, apartment_id, content, created_at, updated_at
				FROM notes
				WHERE username = ?
				ORDER BY created_at DESC
				""";

		try (Connection con = DBConnection.establish();
			 PreparedStatement statement = con.prepareStatement(sql)) {
			statement.setString(1, currentUser.getUsername());
			try (ResultSet rs = statement.executeQuery()) {
				while (rs.next()) {
					notesModel.addRow(new Object[] {
							rs.getInt("note_id"),
							rs.getInt("apartment_id"),
							rs.getString("content"),
							rs.getTimestamp("created_at"),
							rs.getTimestamp("updated_at")
					});
				}
			}
		} catch (SQLException e) {
			showError("Cannot load notes", e);
		}
	}

	private void refreshDashboard() {
		int total = apartmentRows.size();
		double totalPrice = 0;
		Map<String, Integer> cityCount = new HashMap<>();
		Map<String, Integer> categoryCount = new HashMap<>();

		for (ApartmentRow row : apartmentRows) {
			totalPrice += row.price;
			cityCount.put(row.city, cityCount.getOrDefault(row.city, 0) + 1);
			categoryCount.put(row.category, categoryCount.getOrDefault(row.category, 0) + 1);
		}

		String topCity = "-";
		int topCityCount = 0;
		for (Map.Entry<String, Integer> entry : cityCount.entrySet()) {
			if (entry.getValue() > topCityCount) {
				topCity = entry.getKey();
				topCityCount = entry.getValue();
			}
		}

		metricTotalListings.setText(String.valueOf(total));
		metricAveragePrice.setText(total == 0 ? "0" : PRICE_FORMAT.format(totalPrice / total) + " B");
		metricFavorites.setText(String.valueOf(favoritesModel.getRowCount()));
		metricTopCity.setText(topCity);

		StringBuilder breakdown = new StringBuilder();
		categoryCount.entrySet().stream()
				.sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
				.forEach(entry -> breakdown
						.append(String.format("%-12s : %d listing(s)%n", entry.getKey(), entry.getValue())));

		if (breakdown.length() == 0) {
			breakdown.append("No category data available.");
		}
		metricCategoryBreakdown.setText(breakdown.toString());
	}

	private List<ApartmentRow> fetchApartments() throws SQLException {
		String sql = """
				SELECT a.apartment_id, a.address, a.city, a.price, a.bedrooms, a.size, a.category,
					   COALESCE(GROUP_CONCAT(am.name ORDER BY am.name SEPARATOR ', '), '-') AS amenities
				FROM apartment a
				LEFT JOIN apartmentAmenities aa ON aa.apartment_id = a.apartment_id
				LEFT JOIN amenities am ON am.amenity_id = aa.amenity_id
				GROUP BY a.apartment_id, a.address, a.city, a.price, a.bedrooms, a.size, a.category
				ORDER BY a.apartment_id
				""";

		List<ApartmentRow> rows = new ArrayList<>();
		try (Connection con = DBConnection.establish();
			 PreparedStatement statement = con.prepareStatement(sql);
			 ResultSet rs = statement.executeQuery()) {
			while (rs.next()) {
				rows.add(new ApartmentRow(
						rs.getInt("apartment_id"),
						rs.getString("address"),
						rs.getString("city"),
						rs.getDouble("price"),
						rs.getInt("bedrooms"),
						rs.getDouble("size"),
						rs.getString("category"),
						rs.getString("amenities")
				));
			}
		}
		return rows;
	}

	private List<String> fetchAllAmenities() {
		List<String> amenities = new ArrayList<>();
		String sql = "SELECT name FROM amenities ORDER BY name";
		try (Connection con = DBConnection.establish();
			 PreparedStatement statement = con.prepareStatement(sql);
			 ResultSet rs = statement.executeQuery()) {
			while (rs.next()) {
				amenities.add(rs.getString("name"));
			}
		} catch (SQLException e) {
			showError("Cannot load amenities", e);
		}
		return amenities;
	}

	private void exportVisibleListingsToCSV() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Export Listings to CSV");
		chooser.setSelectedFile(new File("apartment-listings.csv"));

		int choice = chooser.showSaveDialog(this);
		if (choice != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File output = chooser.getSelectedFile();
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(output))) {
			writer.write("ID,Address,City,Price(B VND),Bedrooms,Size(sqm),Category,Amenities");
			writer.newLine();

			for (int viewRow = 0; viewRow < listingsTable.getRowCount(); viewRow++) {
				int modelRow = listingsTable.convertRowIndexToModel(viewRow);
				writer.write(escapeCsv(String.valueOf(listingsModel.getValueAt(modelRow, 0))));
				writer.write(',');
				writer.write(escapeCsv(String.valueOf(listingsModel.getValueAt(modelRow, 1))));
				writer.write(',');
				writer.write(escapeCsv(String.valueOf(listingsModel.getValueAt(modelRow, 2))));
				writer.write(',');
				writer.write(escapeCsv(String.valueOf(listingsModel.getValueAt(modelRow, 3))));
				writer.write(',');
				writer.write(escapeCsv(String.valueOf(listingsModel.getValueAt(modelRow, 4))));
				writer.write(',');
				writer.write(escapeCsv(String.valueOf(listingsModel.getValueAt(modelRow, 5))));
				writer.write(',');
				writer.write(escapeCsv(String.valueOf(listingsModel.getValueAt(modelRow, 6))));
				writer.write(',');
				writer.write(escapeCsv(String.valueOf(listingsModel.getValueAt(modelRow, 7))));
				writer.newLine();
			}

			setStatus("CSV exported to: " + output.getAbsolutePath());
			JOptionPane.showMessageDialog(this, "Exported to: " + output.getAbsolutePath(), "Export Success", JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException e) {
			showError("Cannot export CSV", e);
		}
	}

	private String escapeCsv(String value) {
		String escaped = value.replace("\"", "\"\"");
		return "\"" + escaped + "\"";
	}

	private Object[] toApartmentTableRow(ApartmentRow row) {
		return new Object[] {
				row.id,
				row.address,
				row.city,
				row.price,
				row.bedrooms,
				row.size,
				row.category,
				row.amenities
		};
	}

	private JButton createNavButton(String text, String cardName) {
		JButton button = createActionButton(text, new Color(41, 70, 112), Color.WHITE);
		button.addActionListener(event -> {
			contentCardLayout.show(contentCards, cardName);
			setStatus("Switched to " + text + " view.");
		});
		button.setAlignmentX(Component.LEFT_ALIGNMENT);
		return button;
	}

	private DefaultTableModel createReadOnlyModel(String[] columns, Class<?>[] classes) {
		return new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return classes[columnIndex];
			}
		};
	}

	private JTable createStyledTable(DefaultTableModel model) {
		JTable table = new JTable(model);
		table.setFont(FONT_BODY);
		table.setRowHeight(30);
		table.setGridColor(new Color(233, 239, 247));
		table.setShowHorizontalLines(true);
		table.setShowVerticalLines(false);
		table.setSelectionBackground(new Color(218, 238, 255));
		table.setSelectionForeground(new Color(20, 35, 60));
		table.setFillsViewportHeight(true);
		table.setAutoCreateRowSorter(true);

		JTableHeader header = table.getTableHeader();
		header.setFont(new Font("Segoe UI", Font.BOLD, 13));
		header.setBackground(new Color(227, 236, 247));
		header.setForeground(new Color(27, 43, 65));
		header.setPreferredSize(new Dimension(header.getPreferredSize().width, 36));

		DefaultTableCellRenderer doubleRenderer = new DefaultTableCellRenderer() {
			@Override
			protected void setValue(Object value) {
				if (value instanceof Number) {
					setText(PRICE_FORMAT.format(((Number) value).doubleValue()));
				} else {
					super.setValue(value);
				}
			}
		};
		doubleRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

		table.setDefaultRenderer(Double.class, doubleRenderer);
		table.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
			{
				setHorizontalAlignment(SwingConstants.CENTER);
			}
		});

		return table;
	}

	private JButton createActionButton(String text, Color background, Color foreground) {
		JButton button = new JButton(text);
		button.setBackground(background);
		button.setForeground(foreground);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setFont(FONT_BODY_BOLD);
		button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		button.setBorder(new EmptyBorder(10, 14, 10, 14));
		return button;
	}

	private JTextField createTextField(String tooltip) {
		JTextField field = new JTextField();
		styleInput(field);
		field.setToolTipText(tooltip);
		return field;
	}

	private void styleInput(JComponent component) {
		component.setFont(FONT_BODY);
		component.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(213, 223, 235)),
				new EmptyBorder(10, 12, 10, 12)));
		component.setBackground(Color.WHITE);
	}

	private void styleCombo(JComboBox<?> combo) {
		combo.setFont(FONT_BODY);
		combo.setBackground(Color.WHITE);
		combo.setBorder(BorderFactory.createLineBorder(new Color(213, 223, 235)));
	}

	private void styleSpinner(JSpinner spinner) {
		spinner.setFont(FONT_BODY);
		spinner.setBorder(BorderFactory.createLineBorder(new Color(213, 223, 235)));
		JComponent editor = spinner.getEditor();
		if (editor instanceof JSpinner.DefaultEditor defaultEditor) {
			defaultEditor.getTextField().setFont(FONT_BODY);
			defaultEditor.getTextField().setBorder(new EmptyBorder(8, 10, 8, 10));
		}
	}

	private JLabel labelFor(String text) {
		JLabel label = new JLabel(text);
		label.setFont(FONT_BODY_BOLD);
		label.setForeground(new Color(44, 62, 80));
		return label;
	}

	private void addDocumentListener(JTextField field, Runnable callback) {
		field.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				callback.run();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				callback.run();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				callback.run();
			}
		});
	}

	private void setStatus(String message) {
		if (statusLabel != null) {
			statusLabel.setText(message);
		}
	}

	private void showError(String title, Exception exception) {
		String message = exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage();
		JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
	}
}
