import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Blackjack {
    private class Card {
        final String PATH_CARD = "./resources/cards/";

        private String value;
        private String type;

        public Card() {}

        public Card(String value, String type) {
            this.value = value;
            this.type = type;
        }

        public String toString() {
            return value + "-" + type;
        }

        public int getValue() {
            if ("AJQK".contains(value)) {
                if (value == "A") return 11;
                return 10;
            }
            return Integer.parseInt(value);
        }

        public boolean isAce() {
            return value == "A";
        }

        public String getImagePath() {
            return PATH_CARD + toString() + ".png";
        }
    }

    ArrayList <Card> deck = new ArrayList <Card>();
    ArrayList <Card> dealerHand = new ArrayList <Card>();
    ArrayList <Card> playerHand = new ArrayList <Card>();


    Card hiddenCard = new Card();
    Random random = new Random();

    int dealerSum = 0;
    int dealerAceCount = 0;
    int playerSum = 0;
    int playerAceCount = 0;

    // window
    Color background = new Color(0x35654D);

    final int screenWidth = 600;
    final int screenHeight = 600;

    final int cardWidth = 110;
    final int cardHeight = 154;

    JFrame window = new JFrame();
    JPanel panel = new JPanel() {
        @Override
        public void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            try {
                // draw hidden card
                Image imageHiddenCard = new ImageIcon(getClass().getResource("./resources/cards/BACK.png")).getImage();

                if (!stayButton.isEnabled()) {
                    imageHiddenCard = new ImageIcon(getClass().getResource(hiddenCard.getImagePath())).getImage();
                }
                graphics.drawImage(imageHiddenCard, 20, 20, cardWidth, cardHeight, null);

                // draw dealer's hand
                for (int i = 0; i < dealerHand.size(); ++i) {
                    Card card = dealerHand.get(i);

                    Image imgCard = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    graphics.drawImage(imgCard, cardWidth + 25 + (cardWidth + 5)*i, 20, cardWidth, cardHeight, null);
                }

                // draw player's hand
                for (int i = 0; i < playerHand.size(); ++i) {
                    Card card = playerHand.get(i);

                    Image imgCard = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    graphics.drawImage(imgCard, 20 + (cardWidth + 5)*i, 320, cardWidth, cardHeight, null);
                }

                if (!stayButton.isEnabled()) {
                    dealerSum = reduceDealerAce();
                    playerSum = reducePlayerAce();

                    String message = null;
                    if (playerSum > 21) {
                        message = "You Lose!";
                    } else if (dealerSum > 21) {
                        message = "You Win!";
                    } else if (playerSum == dealerSum) {
                        message = "Tie!";
                    } else if (playerSum > dealerSum) {
                        message = "You Win!";
                    } else if (playerSum < dealerSum) {
                        message = "You Lose!";
                    }

                    graphics.setFont(new Font("Arial", Font.PLAIN, 30));
                    graphics.setColor(Color.WHITE);
                    graphics.drawString(message, 220, 250);
                }

            } catch(Exception e) {
                e.printStackTrace();
            }

        }
    };

    JPanel buttonPanel = new JPanel();
    Button hitButton = new Button();
    Button stayButton = new Button();

    public Blackjack() {
        startGame();

        window.setTitle("BlackJack");
        window.setSize(screenWidth, screenHeight);
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);

        panel.setLayout(new BorderLayout());
        panel.setBackground(background);
        window.add(panel);

        hitButton.setLabel("Hit");
        hitButton.setFocusable(false);
        buttonPanel.add(hitButton);

        stayButton.setLabel("Stay");
        stayButton.setFocusable(false);
        buttonPanel.add(stayButton);
        window.add(buttonPanel, BorderLayout.SOUTH);

        hitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Card card = deck.remove(deck.size() - 1);
                playerSum += card.getValue();
                playerAceCount += card.isAce() ? 1 : 0;
                playerHand.add(card);

                if (reducePlayerAce() > 21) {
                    hitButton.setEnabled(false);
                }

                panel.repaint();
            }
        });

        stayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hitButton.setEnabled(false);
                stayButton.setEnabled(false);

                while (dealerSum < 17) {
                    Card card = deck.remove(deck.size() - 1);
                    dealerSum += card.getValue();
                    dealerAceCount += card.isAce() ? 1 : 0;
                    dealerHand.add(card);
                }

                panel.repaint();
            }
        });

        panel.repaint();
        window.setVisible(true);
    }

    public void startGame() {
        // deck
        buildDeck();
        shuffleDeck();

        // dealer
        hiddenCard = deck.remove(deck.size() - 1);
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;

        Card card = deck.remove(deck.size() - 1);
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;
        dealerHand.add(card);

        // player
        for (int i = 0; i < 2; ++i) {
            card = deck.remove(deck.size() - 1);
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
        }
    }


    public void buildDeck() {
        String[] value = {
            "A", "2", "3",
            "4", "5", "6", "7",
            "8", "9", "10", "J",
            "Q", "K"
        };

        String[] type = {"C", "D", "H", "S"};

        for (int i = 0; i < type.length; ++i) {
            for (int j = 0; j < value.length; ++j) {
                Card card = new Card(value[j], type[i]);
                deck.add(card);
            }
        }
    }

    public void shuffleDeck() {
        for (int i = 0; i < deck.size(); ++i) {
            int j = random.nextInt(deck.size());
            Card currCard = deck.get(i);
            Card randomCard = deck.get(j);
            deck.set(i, randomCard);
            deck.set(j, currCard);
        }
    }

    public int reducePlayerAce() {
        while (playerSum > 21 && playerAceCount > 0) {
            playerSum -= 10;
            playerAceCount -= 1;
        }

        return playerSum;
    }

    public int reduceDealerAce() {
        while (dealerSum > 21 && dealerAceCount > 0) {
            dealerSum -= 10;
            dealerAceCount -= 1;
        }

        return dealerSum;
    }
}
