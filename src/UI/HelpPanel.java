package UI;

import Utils.GameConfig;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class HelpPanel extends JPanel {

    private final JButton backBtn;
    private final Image backgroundImage;

    public HelpPanel(ActionListener backAction) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        setOpaque(false);

        // Carrega a imagem de fundo
        String bgPath = GameConfig.BACKGROUND_PATH + GameConfig.SETUP_BACKGROUND_TEXTURE;
        backgroundImage = Utils.ImageLoader.getImage(bgPath);

        // Top Panel: Title com estilo melhorado
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel lblTitle = new JLabel("AJUDA E SUPORTE");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        topPanel.add(lblTitle);
        add(topPanel, BorderLayout.NORTH);

        // Center Panel: Content compacto sem scroll
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(5, 10, 5, 10));

        // Game Description com card estilizado
        JPanel descCard = createCard();
        descCard.setLayout(new BorderLayout(5, 3));

        JLabel descTitle = new JLabel("Sobre o Jogo");
        descTitle.setForeground(new Color(255, 215, 0));
        descTitle.setFont(new Font("Arial", Font.BOLD, 16));
        descCard.add(descTitle, BorderLayout.NORTH);

        JTextArea descArea = new JTextArea(
                "Bem-vindo ao Labirinto da Glória! Este é um jogo de estratégia onde o objetivo é ser o primeiro a alcançar o tesouro no centro do labirinto. Os jogadores devem navegar através de um labirinto complexo, usando itens e evitando armadilhas."
        );
        styleTextArea(descArea, 15);
        descCard.add(descArea, BorderLayout.CENTER);
        centerPanel.add(descCard);
        centerPanel.add(Box.createVerticalStrut(8));

        // Rules com card estilizado
        JPanel rulesCard = createCard();
        rulesCard.setLayout(new BorderLayout(5, 3));

        JLabel rulesTitle = new JLabel("Regras");
        rulesTitle.setForeground(new Color(255, 215, 0));
        rulesTitle.setFont(new Font("Arial", Font.BOLD, 16));
        rulesCard.add(rulesTitle, BorderLayout.NORTH);

        JTextArea rulesArea = new JTextArea(
                """
                • Cada jogador joga à vez, lançando os dados para se mover.
                • O objetivo é chegar à sala central (Tesouro).
                • Utilize alavancas para abrir/fechar caminhos.
                • Colete itens para ganhar vantagens.
                """
        );
        styleTextArea(rulesArea, 15);
        rulesCard.add(rulesArea, BorderLayout.CENTER);
        centerPanel.add(rulesCard);
        centerPanel.add(Box.createVerticalStrut(8));

        // Split Panel for Items and Events
        JPanel infoPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        infoPanel.setOpaque(false);
        infoPanel.setMaximumSize(new Dimension(1200, 280));

        // Left: Items
        JPanel itemsCard = createCard();
        itemsCard.setLayout(new BorderLayout(5, 5));

        JLabel itemsTitle = new JLabel("Itens Colecionáveis");
        itemsTitle.setForeground(new Color(255, 215, 0));
        itemsTitle.setFont(new Font("Arial", Font.BOLD, 16));
        itemsCard.add(itemsTitle, BorderLayout.NORTH);

        JPanel itemsContent = new JPanel();
        itemsContent.setLayout(new BoxLayout(itemsContent, BoxLayout.Y_AXIS));
        itemsContent.setOpaque(false);
        itemsContent.setBorder(new EmptyBorder(5, 5, 5, 5));

        addItemInfoWithIcon(itemsContent, "Picareta", "Permite quebrar paredes quebráveis para criar atalhos.", GameConfig.PICKAXE_TEXTURE);
        itemsContent.add(Box.createVerticalStrut(12));
        addItemInfoWithIcon(itemsContent, "Pérola do Ender", "Permite teletransportar-se para uma posição aleatória ou específica.", GameConfig.ENDERPEARL_TEXTURE);

        itemsCard.add(itemsContent, BorderLayout.CENTER);
        infoPanel.add(itemsCard);

        // Right: Random Events
        JPanel eventsCard = createCard();
        eventsCard.setLayout(new BorderLayout(5, 5));

        JLabel eventsTitle = new JLabel("Eventos Aleatórios");
        eventsTitle.setForeground(new Color(255, 215, 0));
        eventsTitle.setFont(new Font("Arial", Font.BOLD, 16));
        eventsCard.add(eventsTitle, BorderLayout.NORTH);

        JPanel eventsContent = new JPanel();
        eventsContent.setLayout(new BoxLayout(eventsContent, BoxLayout.Y_AXIS));
        eventsContent.setOpaque(false);
        eventsContent.setBorder(new EmptyBorder(5, 5, 5, 5));

        addEventInfoWithIcon(eventsContent, "Inversão de Sinal", "Inverte o estado de todas as alavancas no tabuleiro.", GameConfig.REDSTONE_BLOCK_TEXTURE);
        eventsContent.add(Box.createVerticalStrut(12));
        addEventInfoWithIcon(eventsContent, "Redução de Velocidade", "Força 3 movimentos ao jogador devido à redução de velocidade.", GameConfig.SOUL_SAND_TEXTURE);

        eventsCard.add(eventsContent, BorderLayout.CENTER);
        infoPanel.add(eventsCard);

        centerPanel.add(infoPanel);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel: Back Button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        bottomPanel.setOpaque(false);

        backBtn = createButton("Voltar");
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> {
            Utils.SoundPlayer.playClick();
            backAction.actionPerformed(e);
        });

        bottomPanel.add(backBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(0, 0, 0, 160));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                g2d.setColor(new Color(255, 215, 0, 100));
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 15, 15);

                g2d.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(8, 15, 8, 15));
        return card;
    }

    private void styleTextArea(JTextArea area, int fontSize) {
        area.setWrapStyleWord(true);
        area.setLineWrap(true);
        area.setOpaque(false);
        area.setEditable(false);
        area.setForeground(new Color(230, 230, 230));
        area.setFont(new Font("Arial", Font.PLAIN, fontSize));
        area.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Aumenta o espaçamento entre linhas
        FontMetrics fm = area.getFontMetrics(area.getFont());
        int lineHeight = (int) (fm.getHeight() * 1.3);
        area.setFont(new Font("Arial", Font.PLAIN, fontSize));
    }

    private void addItemInfoWithIcon(JPanel panel, String title, String desc, String iconTexture) {
        JPanel itemPanel = new JPanel(new BorderLayout(10, 0));
        itemPanel.setOpaque(false);
        itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        itemPanel.setMaximumSize(new Dimension(500, 80));

        // Ícone do item
        String iconPath = GameConfig.ITENS_PATH + iconTexture;
        BufferedImage iconImg = Utils.ImageLoader.getImage(iconPath);
        JLabel iconLabel = new JLabel();
        if (iconImg != null) {
            Image scaledIcon = iconImg.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(scaledIcon));
        }
        iconLabel.setVerticalAlignment(SwingConstants.TOP);
        itemPanel.add(iconLabel, BorderLayout.WEST);

        // Conteúdo (título + descrição)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 15));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);

        contentPanel.add(Box.createVerticalStrut(3));

        JTextArea descLabel = new JTextArea(desc);
        descLabel.setWrapStyleWord(true);
        descLabel.setLineWrap(true);
        descLabel.setOpaque(false);
        descLabel.setEditable(false);
        descLabel.setForeground(new Color(200, 200, 200));
        descLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(descLabel);

        itemPanel.add(contentPanel, BorderLayout.CENTER);
        panel.add(itemPanel);
    }

    private void addEventInfoWithIcon(JPanel panel, String title, String desc, String iconTexture) {
        JPanel eventPanel = new JPanel(new BorderLayout(10, 0));
        eventPanel.setOpaque(false);
        eventPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        eventPanel.setMaximumSize(new Dimension(500, 80));

        // Ícone do evento
        String iconPath = GameConfig.TEXTURES_PATH + iconTexture;
        BufferedImage iconImg = Utils.ImageLoader.getImage(iconPath);
        JLabel iconLabel = new JLabel();
        if (iconImg != null) {
            Image scaledIcon = iconImg.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(scaledIcon));
        }
        iconLabel.setVerticalAlignment(SwingConstants.TOP);
        eventPanel.add(iconLabel, BorderLayout.WEST);

        // Conteúdo (título + descrição)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 15));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);

        contentPanel.add(Box.createVerticalStrut(3));

        JTextArea descLabel = new JTextArea(desc);
        descLabel.setWrapStyleWord(true);
        descLabel.setLineWrap(true);
        descLabel.setOpaque(false);
        descLabel.setEditable(false);
        descLabel.setForeground(new Color(200, 200, 200));
        descLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(descLabel);

        eventPanel.add(contentPanel, BorderLayout.CENTER);
        panel.add(eventPanel);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text) {
            private boolean isHovered = false;

            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        isHovered = true;
                        setForeground(Color.decode(GameConfig.TEXT_COLOR_HOVER_HEX));
                        repaint();
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        isHovered = false;
                        setForeground(Color.WHITE);
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                BufferedImage btnImg = Utils.ImageLoader.getImage(GameConfig.UI_PATH_TEXTURE + GameConfig.SHORT_BUTTON_TEXTURE);
                BufferedImage hoverBtnImg = Utils.ImageLoader.getImage(GameConfig.UI_PATH_TEXTURE + GameConfig.HOVER_SHORT_BUTTON_TEXTURE);

                if (btnImg != null) {
                    if (isHovered && hoverBtnImg != null) {
                        g.drawImage(hoverBtnImg, 0, 0, getWidth(), getHeight(), null);
                    } else {
                        g.drawImage(btnImg, 0, 0, getWidth(), getHeight(), null);
                    }
                }
                super.paintComponent(g);
            }
        };

        btn.setPreferredSize(new Dimension(200, 50));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);

        return btn;
    }
}
