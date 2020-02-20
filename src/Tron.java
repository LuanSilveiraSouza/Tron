import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Tron extends JPanel implements Runnable, KeyListener {

	Random rnd = new Random(); // Randomificador

	Color azul = new Color(0, 200, 255);
	Color verde = new Color(0, 100, 0);
	Font f = new Font("Arial", Font.BOLD, 15);
	
	int tile = 30; // Tamanho do Player, tanto em X como em Y

	int posX[] = new int[tile*tile];
	int posY[] = new int[tile*tile];
	int tamanhoAtual = 4;
	
	BufferedImage motoUp;
	BufferedImage motoDown;
	BufferedImage motoLeft;
	BufferedImage motoRight;
	BufferedImage battery;
	
	int auxX;
	int auxY;

	int tempo = 300;

	boolean perdeu = false;

	boolean cima = false; // Variáveis booleanas para controlar os movimentos do Player; Indicar para qual lado ele está se locomovendo				
	boolean baixo = false;
	boolean esquerda = false;
	boolean direita = false;

	int comidaPosX = rnd.nextInt(19); // Posição X da "comida"
	int comidaPosY = rnd.nextInt(19); // Posição Y da "comida"

	public static void main(String[] args) {

		JFrame janela = new JFrame("TRON"); // Criando JFrame (Janela) e
												// setando as configurações dela
		janela.setSize(608, 628);
		janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		janela.setLocationRelativeTo(null);
		janela.setResizable(false);
		janela.setVisible(true);

		Tron construtor = new Tron(); // Criando o construtor (ou canvas): a
										// tela onde é possível desenhar
		construtor.setSize(600, 600);
		construtor.setVisible(true);

		janela.add(construtor); // Adicionando o construtor na janela
		janela.addKeyListener(construtor); // Adicionando um KeyListener na
											// janela para detectar teclas do
											// teclado

	}

	public Tron() {
		Thread thread = new Thread(this); // Criando a Thread do jogo, onde ele
											// irá rodar
		thread.start(); // Iniciando a Thread
	}

	@Override
	public void run() { // Método Run, onde está o princípio básico de qualquer
						// jogo
		posX[0] = tile*3; // Posição X do Player
		posY[0] = tile; // Posição Y do Player

		direita = true;
	
		loadImg();
		
		int resposta = JOptionPane.showOptionDialog(null,
				"Bem Vindo ao TRON! Quer jogar?", "TRON", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

		if (resposta == JOptionPane.YES_OPTION) {
			do { // Um loop que a cada repetição
				if (!perdeu) {
					atualizar(); // Atualiza informações
					repaint(); // Redesenha a tela
					dorme(); // Pausa por um breve momento para o jogo não ser
								// tão rápido
				} else {
					resposta = JOptionPane.showOptionDialog(null,
							"WASTED \nPontuação: "+ tamanhoAtual +"\nJogar Novamente?", "Jogar Novamente?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
					if (resposta == JOptionPane.YES_OPTION) {
						perdeu = false;
						tamanhoAtual = 4;
						posX[0] = tile*3;
						posY[0] = tile;
						tempo = 300;
						reset();
						direita = true;
					} else if (resposta == JOptionPane.NO_OPTION) {
						System.exit(ABORT);
					}
				}
			} while (true);
		} else if (resposta == JOptionPane.NO_OPTION) {
			System.exit(ABORT);
		}
	}

	public void atualizar() { // Método atualizar
		auxX = posX[0];
		auxY = posY[0];

		movimentar();
		
		if (cima && posY[0] > 0) { // Verificando se o Player QUER e PODE ir para cima
			posY[0] -= tile;
		} else if (cima && posY[0] <= 0) {
			posY[0] = 600 - tile;
		}

		if (baixo && posY[0] + tile < 600) { // Verificando se o Player QUER e PODE ir para baixo
			posY[0] += tile;
		} else if (baixo && posY[0] >= 600 - tile) {
			posY[0] = 0;
		}

		if (esquerda && posX[0] > 0) { // Verificando se o Player QUER e PODE ir para esquerda
			posX[0] -= tile;
		} else if (esquerda && posX[0] <= 0) {
			posX[0] = 600 - tile;
		}

		if (direita && posX[0] + tile < 600) { // Verificando se o Player QUER e PODE ir para direita
			posX[0] += tile;
		} else if (direita && posX[0] >= 600 - tile) {
			posX[0] = 0;
		}
	
		for (int i = 1; i < tamanhoAtual; i++) {
			if (posX[0] == posX[i] && posY[0] == posY[i]) {
				perdeu = true;
				break;
			}
		}
		if (posX[0] == (comidaPosX * tile) && posY[0] == (comidaPosY * tile)) {
			for (int i = 0; i < tamanhoAtual; i++) {
				while (posX[i] == (comidaPosX * tile)
						&& posY[i] == (comidaPosY * tile)) { // Verificando se o Player e a Comida estão na mesma posição
					comidaPosX = rnd.nextInt(19); // Nova posição X e Y da comida
					comidaPosY = rnd.nextInt(19);
				}

			}
			tamanhoAtual++;
			posX[tamanhoAtual] = -20;
			posY[tamanhoAtual] = -20;
			if (tempo > 30 && tamanhoAtual % 3 == 0) {
				tempo *= 0.9;
				System.out.println(tempo);
			}
			
			
		}

		atualizou = true;
	}

	public void paintComponent(Graphics g2) { // Método onde será desenhado os objetos do jogo
		Graphics2D g = (Graphics2D) g2.create();										
		super.paintComponent(g); // Limpa a tela

		g.setColor(Color.GRAY);
		g.fillRect(0, 0, 620, 620); // Preenche fundo de cinza
		
		g.setColor(azul);
		for (int i = 1; i < 600 / tile; i++) {
			g.drawLine(0, tile * i, 600, tile * i); // Desenha linhas
													// horizontais
			g.drawLine(tile * i, 0, tile * i, 600); // Desenha linhas verticais
		}
			
		if (tempo > 200) 
			g.setColor(Color.white);
		else if (tempo <= 200 && tempo > 150)
			g.setColor(Color.yellow);
		else if (tempo <= 150 && tempo > 100)
			g.setColor(Color.orange);
		else if (tempo <= 100 && tempo > 50)
			g.setColor(Color.red);
		else if (tempo <= 50)
			g.setColor(Color.black);
		g.setFont(f);
		g.drawString(""+tamanhoAtual, 550, 13);
		
		if (direita) {
			g.drawImage(motoRight, posX[1], posY[1], tile*2, tile, null);
		} else if (esquerda) {
			g.drawImage(motoLeft, posX[0], posY[1], tile*2, tile, null);
		} else if (cima) {
			g.drawImage(motoUp, posX[1], posY[0], tile, tile*2, null);
		} else if (baixo) {
			g.drawImage(motoDown, posX[1], posY[1], tile, tile*2, null);
		}
		
		for (int i = 2; i < tamanhoAtual; i++) {
			g.setColor(azul);
			g.fillRect(posX[i], posY[i], tile, tile); // Desenha o corpo na posição X e Y, sempre com tamanho 20 tanto no X e no Y(quadrado)
		}
		
		g.drawImage(battery, comidaPosX * tile + 7, comidaPosY * tile, tile/2, tile, null); 
	}

	public void dorme() { // Método para pausar o programa por milissegundos,
							// garantindo fluidez
		try {
			Thread.sleep(tempo);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	boolean atualizou = false;
	
	@Override
	public void keyPressed(KeyEvent e) { // Verifica qual tecla o usuário está pressionando
		
		if (atualizou ) {
		
		if (esquerda || direita) {
			if (e.getKeyCode() == KeyEvent.VK_W) {
				reset();
				cima = true;
			} else if (e.getKeyCode() == KeyEvent.VK_S) {
				reset();
				baixo = true;
			}
		}

		if (cima || baixo) {
			if (e.getKeyCode() == KeyEvent.VK_A) {
				reset();
				esquerda = true;
			} else if (e.getKeyCode() == KeyEvent.VK_D) {
				reset();
				direita = true;
			}
		}
		
		atualizou = false;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	public void reset() { // Método para resetar todas as direções (facilitador)
		cima = false;
		baixo = false;
		esquerda = false;
		direita = false;
	}

	public void movimentar() {
		for (int i = 1; i < tamanhoAtual; i++) {
			auxX += posX[i];
			posX[i] = auxX - posX[i];
			auxX -= posX[i];

			auxY += posY[i];
			posY[i] = auxY - posY[i];
			auxY -= posY[i];
		}
	}
	
	public void loadImg() {
		try {
			motoUp = ImageIO.read(getClass().getResource ("/img/TronUp.png"));
		} catch (IOException e) {
			System.out.println("Não carregou a img");
			e.printStackTrace();
		}
		try {
			motoDown = ImageIO.read(getClass().getResource ("/img/TronDown.png"));
		} catch (IOException e) {
			System.out.println("Não carregou a img");
			e.printStackTrace();
		}
		try {
			motoLeft = ImageIO.read(getClass().getResource ("/img/TronLeft.png"));
		} catch (IOException e) {
			System.out.println("Não carregou a img");
			e.printStackTrace();
		}
		try {
			motoRight = ImageIO.read(getClass().getResource ("/img/TronRight.png"));
		} catch (IOException e) {
			System.out.println("Não carregou a img");
			e.printStackTrace();
		}
		try {
			battery = ImageIO.read(getClass().getResource ("/img/battery.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}