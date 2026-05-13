package com.oop.project.ui.components;

import com.oop.project.model.Note;
import com.oop.project.service.NoteManagement;
import com.oop.project.ui.Theme;
import com.oop.project.util.SessionManager;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.awt.event.ActionEvent;

public class NotesDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final int apartmentId;
    private final NoteManagement noteService;
    private JPanel cardsPanel;
    private JTextField newNoteField;
    private JButton addButton;

    public NotesDialog(Frame owner, int apartmentId) {
        super(owner, "Notes for Apartment #" + apartmentId, true);
        this.apartmentId = apartmentId;
        this.noteService = new NoteManagement();
        setResizable(true);
        initComponents();
        layoutComponents();
        loadNotes();
        setSize(550, 500);
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        cardsPanel = new JPanel();
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        cardsPanel.setBackground(Theme.BACKGROUND);

        newNoteField = new JTextField(30);
        addButton = new StyledButton("Add Note", Theme.PRIMARY);
        addButton.addActionListener(e -> addNote());
    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Theme.SURFACE);

        // Header with apartment info
        JLabel headerLabel = new JLabel("Apartment #" + apartmentId);
        headerLabel.setFont(Theme.HEADER_FONT.deriveFont(16f));
        headerLabel.setForeground(Theme.PRIMARY);
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(Theme.SURFACE);
        headerPanel.add(headerLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Theme.BACKGROUND);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(Theme.SURFACE);
        bottomPanel.add(new JLabel("New Note:"));
        bottomPanel.add(newNoteField);
        bottomPanel.add(addButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void loadNotes() {
        cardsPanel.removeAll();
        try {
            List<Note> notes = noteService.getNotesForApartment(apartmentId);
            for (Note note : notes) {
                NoteCard card = new NoteCard(note);
                cardsPanel.add(card);
                cardsPanel.add(Box.createVerticalStrut(8));
            }
        } catch (SQLException | SecurityException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load notes: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private void addNote() {
        String content = newNoteField.getText().trim();
        if (content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Note cannot be empty.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            noteService.addNote(apartmentId, content);
            newNoteField.setText("");
            loadNotes();
        } catch (SQLException | SecurityException ex) {
            JOptionPane.showMessageDialog(this, "Failed to add note: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Card for a single note ────────────────────────────────────
    private class NoteCard extends JPanel {
        private final Note note;
        private JLabel authorLabel;
        private JTextArea contentArea;
        private JLabel dateLabel;
        private JButton editBtn;
        private JButton deleteBtn;

        public NoteCard(Note note) {
            this.note = note;
            setLayout(new BorderLayout(10, 5));
            setBorder(new CompoundBorder(
                    new LineBorder(new Color(200, 210, 220), 1, true),
                    new EmptyBorder(10, 10, 10, 10)
            ));
            setBackground(Color.WHITE);
            // No maximum height – let content decide
            initComponents();
            assembleLayout();
            applyPermissions();
        }

        private void initComponents() {
            // Author: top‑left, bold
            String authorText = note.getUsername();
            authorLabel = new JLabel(authorText);
            authorLabel.setFont(Theme.BODY_FONT.deriveFont(Font.BOLD, 13f));
            authorLabel.setForeground(Theme.TEXT_PRIMARY);
            authorLabel.setHorizontalAlignment(SwingConstants.LEFT);

            // Content: wrapped text, set preferred width to force wrapping
            contentArea = new JTextArea(note.getContent());
            contentArea.setFont(Theme.BODY_FONT);
            contentArea.setForeground(Theme.TEXT_PRIMARY);
            contentArea.setEditable(false);
            contentArea.setLineWrap(true);
            contentArea.setWrapStyleWord(true);
            contentArea.setOpaque(false);
            contentArea.setColumns(30);   // wrap after ~30 characters

            // Date: italic, semi‑transparent
            String formattedDate = note.getUpdatedAt() != null
                    ? note.getUpdatedAt().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                    : "";
            dateLabel = new JLabel(formattedDate);
            dateLabel.setFont(Theme.SMALL_FONT.deriveFont(Font.ITALIC));
            dateLabel.setForeground(new Color(120, 120, 120, 180));

            // Action buttons
            editBtn = new JButton("Edit");
            editBtn.setFont(Theme.SMALL_FONT);
            editBtn.addActionListener(this::editNote);

            deleteBtn = new JButton("Delete");
            deleteBtn.setFont(Theme.SMALL_FONT);
            deleteBtn.addActionListener(this::deleteNote);
        }

        private void assembleLayout() {
            // Top panel: author on the LEFT
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            topPanel.setOpaque(false);
            topPanel.add(authorLabel);

            // Middle: content
            JPanel centerPanel = new JPanel(new BorderLayout());
            centerPanel.setOpaque(false);
            centerPanel.add(contentArea, BorderLayout.CENTER);

            // Bottom: date left, actions right
            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.setOpaque(false);
            bottomPanel.add(dateLabel, BorderLayout.WEST);

            JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
            actionsPanel.setOpaque(false);
            actionsPanel.add(editBtn);
            actionsPanel.add(deleteBtn);
            bottomPanel.add(actionsPanel, BorderLayout.EAST);

            add(topPanel, BorderLayout.NORTH);
            add(centerPanel, BorderLayout.CENTER);
            add(bottomPanel, BorderLayout.SOUTH);
        }

        private void applyPermissions() {
            String currentUser = SessionManager.getCurrentUsername();
            boolean isAuthor = note.getUsername().equals(currentUser);   // only the author
            editBtn.setVisible(isAuthor);
            deleteBtn.setVisible(isAuthor);
        }

        private void editNote(ActionEvent e) {
            String newContent = JOptionPane.showInputDialog(NotesDialog.this,
                    "Edit note:", note.getContent());
            if (newContent != null && !newContent.trim().isEmpty()) {
                try {
                    noteService.updateNote(note.getNoteId(), newContent.trim());
                    loadNotes();
                } catch (SQLException | SecurityException ex) {
                    JOptionPane.showMessageDialog(NotesDialog.this,
                            "Update failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void deleteNote(ActionEvent e) {
            int confirm = JOptionPane.showConfirmDialog(NotesDialog.this,
                    "Delete this note?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    noteService.deleteNote(note.getNoteId());
                    loadNotes();
                } catch (SQLException | SecurityException ex) {
                    JOptionPane.showMessageDialog(NotesDialog.this,
                            "Delete failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}