package Cliente;
import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Position.Bias;

public class InterfazGrafica extends JFrame {

	private JPanel contentPane;
	private MusicClient cliente;
	private JLabel lbCancion, lbArtista;
	private JButton btnPlay, btnNext, btnDesconectar;
	private String cancionNueva, cancionVieja;
	private JList<String> list;
	private JScrollPane scrollPane;
	private boolean aleatorio;
	private JTextField tfFiltrar;
	private String[] canciones;
	boolean inicio;

	/**
	 * Create the frame.
	 */
	public InterfazGrafica() {
		setType(Type.POPUP);
		setBackground(new Color(0, 0, 0));
		setForeground(new Color(0, 0, 0));
		aleatorio = false;
		setTitle("Reproductor de musica");
		cancionVieja = "";
		cliente = new MusicClient("localhost", 7788);
//		cliente = new MusicClient("192.168.1.39", 7788);

		cliente.establecerConexion();
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 702, 523);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(144, 238, 144));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		DefaultListModel<String> modelo = new DefaultListModel<>();
		canciones = cliente.recibirLista();
		for (String s : canciones) {
			modelo.addElement(s.split("\\.")[0]);
		}

		scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(440, 111, 244, 296);
		contentPane.add(scrollPane);

		list = new JList<>();
		list.setVisibleRowCount(4);
		scrollPane.setViewportView(list);
		list.setModel(modelo);
		list.setBackground(SystemColor.text);
		list.setFont(new Font("Arial", Font.PLAIN, 20));
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);

		list.setBorder(new LineBorder(new Color(0, 0, 0), 2));

		JLabel lblCanciones = new JLabel("Canciones");
		lblCanciones.setForeground(new Color(0, 0, 0));
		lblCanciones.setFont(new Font("Times New Roman", Font.BOLD, 25));
		lblCanciones.setBounds(493, 23, 109, 38);
		contentPane.add(lblCanciones);

		btnPlay = new JButton("Play");
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(btnPlay.getText().compareTo("Play")==0) {
					reproducir();
					btnPlay.setText("Pause");
				}else {
					pausar();
					btnPlay.setText("Play");
					
				}
				
			}
		});
		btnPlay.setBounds(138, 355, 97, 25);
		contentPane.add(btnPlay);

		btnNext = new JButton(">>");
		btnNext.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				btnPlay.setText("Pause");
				if (aleatorio) {
					siguienteRandom(canciones, modelo);
				} else {
					siguienteOrden(canciones);
				}

			}
		});

		btnNext.setBounds(247, 355, 97, 25);
		contentPane.add(btnNext);

		JButton button = new JButton("<<");
		inicio = false;
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				btnPlay.setText("Pause");
				if (arg0.getClickCount() == 2) {
					cancionAtras(list);
				} else {
					inicio = true;
					volverInicio(list);			
				}
			}
		});

		JRadioButton rdbtnAleatorio = new JRadioButton("Random");
		rdbtnAleatorio.setFont(new Font("Tahoma", Font.PLAIN, 12));
		rdbtnAleatorio.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				aleatorio = !aleatorio;
			}
		});
		rdbtnAleatorio.setToolTipText("Orden aleatorio");
		rdbtnAleatorio.setBounds(257, 393, 73, 25);
		contentPane.add(rdbtnAleatorio);
		button.setBounds(29, 355, 97, 25);
		contentPane.add(button);

		btnDesconectar = new JButton("Desconectar");
		btnDesconectar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				desconectar();
			}
		});
		btnDesconectar.setBounds(474, 425, 128, 25);
		contentPane.add(btnDesconectar);

		lbCancion = new JLabel("Cancion:");
		lbCancion.setForeground(new Color(0, 0, 0));
		lbCancion.setHorizontalAlignment(SwingConstants.CENTER);
		lbCancion.setFont(new Font("Tahoma", Font.BOLD, 20));
		lbCancion.setBounds(31, 263, 378, 38);
		contentPane.add(lbCancion);

		lbArtista = new JLabel("Artista: ");
		lbArtista.setFont(new Font("Tahoma", Font.BOLD, 18));
		lbArtista.setHorizontalAlignment(SwingConstants.CENTER);
		lbArtista.setForeground(new Color(0, 0, 0));
		lbArtista.setBounds(29, 304, 380, 38);
		contentPane.add(lbArtista);

		JLabel lblServidorDeMusica = new JLabel("Servidor de Musica Unirioja");
		lblServidorDeMusica.setForeground(new Color(0, 0, 0));
		lblServidorDeMusica.setFont(new Font("Tahoma", Font.BOLD, 25));
		lblServidorDeMusica.setBounds(12, 13, 352, 48);
		contentPane.add(lblServidorDeMusica);

		tfFiltrar = createTextField();
		tfFiltrar.setBounds(519, 76, 128, 22);
		contentPane.add(tfFiltrar);
		tfFiltrar.setColumns(10);

		JLabel lblFiltrar = new JLabel("Filtrar");
		lblFiltrar.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblFiltrar.setBounds(451, 82, 56, 16);
		contentPane.add(lblFiltrar);



	}

	/**
	 * Reproduce la cancion que esta inmediatamente atras de la que esta sonando
	 * @param list2
	 */
	protected void cancionAtras(JList<String> list2) {
		// TODO Auto-generated method stub
		int posicion = list.getSelectedIndex();
		if (posicion == 0) {
			posicion = list.getModel().getSize() - 1;
		} else {
			posicion = posicion - 1;
		}
		list.setSelectedIndex(posicion);
		reproducir();
	}

	/**
	 * Inicia la cancion que esta sonando actualmente
	 * @param list2
	 */
	protected void volverInicio(JList<String> list2) {
		// TODO Auto-generated method stub
		int s = list2.getNextMatch(cancionNueva, 0, Bias.Forward);
		list2.setSelectedIndex(s);
		cliente.setTiempo(0);
		reproducir();

	}

	/**
	 * Reproduce la siguiente cancion en la lista 
	 * @param listaCanciones
	 */
	protected void siguienteOrden(String[] listaCanciones) {
		// TODO Auto-generated method stub
		int posicion = list.getSelectedIndex();
		if (posicion == list.getModel().getSize() - 1) {
			posicion = 0;
		} else {
			posicion = posicion + 1;
		}
		list.setSelectedIndex(posicion);
		reproducir();
	}

	/**
	 * Reproduce la siguiente cancion de forma aleatoria
	 * @param listaCanciones
	 * @param modelo
	 */
	protected void siguienteRandom(String[] listaCanciones, DefaultListModel<String> modelo) {
		// TODO Auto-generated method stub
		Random generator = new Random();
		int randomIndex = generator.nextInt(listaCanciones.length);
		if (randomIndex == list.getSelectedIndex()) {
			randomIndex++;
		}
		if (randomIndex >= listaCanciones.length) {
			randomIndex = 0;
		}

		list.setSelectedValue(modelo.getElementAt(randomIndex), true);
		cancionNueva = list.getSelectedValue();
		cliente.pedirCancion(cancionNueva);
		cliente.pausarWAV(cancionVieja);
		cliente.reproducirWAV(cancionNueva, cancionVieja, false);
	}
	/**
	 * Para la cancion que esta sonando y se desconecta del servidor
	 */
	protected void desconectar() {
		// TODO Auto-generated method stub
		if (cancionVieja != null)
			cliente.pausarWAV(cancionVieja);
		cliente.desconectar();
		JOptionPane.showMessageDialog(null, "Te has desconectado, hasta la proxima.", "Unirioja Music", 1);
		System.exit(0);
	}

	/**
	 * Pausa la cancion que esta sonando
	 */
	protected void pausar() {
		// TODO Auto-generated method stub
		cancionVieja = cancionNueva;
		if (cancionVieja != null) {
			btnPlay.setEnabled(true);
			cliente.pausarWAV(cancionVieja);
		}

	}

	public void mostrarInterfaz() {
		this.setVisible(true);
	}

	/**
	 * Reproduce la cancion seleccionada en la JList
	 */
	protected void reproducir() {
		if (cancionVieja != null)
			cliente.pausarWAV(cancionVieja);

		cancionNueva = list.getSelectedValue();
		if (cancionNueva != null) {
			cliente.pedirCancion(cancionNueva);
			String[] partes = cancionNueva.split("-");
			lbCancion.setText("Cancion:" + partes[1]);
			lbArtista.setText("Artista: " + partes[0]);
			if(inicio) {
				inicio = false;
				cliente.reproducirWAV(cancionNueva, cancionVieja, true);
			}
			else
				cliente.reproducirWAV(cancionNueva, cancionVieja, false);
		} else {
			JOptionPane.showMessageDialog(null, "Selecciona una cancion de la lista");
		}
	}

	/**
	 * Filtra las canciones a partir de subcadenas que contengan
	 * @param model
	 * @param filter
	 */
	public void filterModel(DefaultListModel<String> model, String filter) {
		for (String s : canciones) {
			String[] partes = s.split("\\.");
			if (!s.toLowerCase().contains(filter.toLowerCase())) {
				if (model.contains(partes[0])) {
					model.removeElement(partes[0]);
				}
			} else {
				if (!model.contains(partes[0])) {
					model.addElement(partes[0]);
				}
			}
		}
	}

	/**
	 * Crea un textField dede el que se filtran las canciones
	 * @return
	 */
	private JTextField createTextField() {

		JTextField field = new JTextField();
		field.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				filter();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				filter();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}

			private void filter() {
				String filter = field.getText();
				filterModel((DefaultListModel<String>) list.getModel(), filter);
			}
		});
		return field;
	}
}

