package Cliente;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import Servidor.Playing;

public class MusicClient {

	private OutputStream os;
	private InputStream is;
	private Socket cliente;
	private int puerto;
	private String ipServidor;
	private Clip clip;
	private Playing hilo = null; 
	private long poscionPause;

	
	/**
	 * Constructor de la clase MusicClient, donde se
	 * inicializan las variables.
	 * @param ipServer
	 * @param puerto
	 */
	public MusicClient(String ipServer, int puerto) {
		this.puerto = puerto;
		this.ipServidor = ipServer;
	}

	/**
	 * Establece la conexion con el servidor con ip ipServidor
	 * a traves del puerto puerto.
	 * Se inicializan las variables os (OutputStream) e is (InputStream)
	 * 
	 * @return true si se ha establecido correctamente, y falso en caso contrario
	 * 
	 * @exception IOException
	 */
	public boolean establecerConexion() {
		try {
			cliente = new Socket(ipServidor, puerto);
			os = cliente.getOutputStream();
			is = cliente.getInputStream();
			poscionPause = 0;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return cliente!=null;
	}

	/**
	 * Devuelve la lista de canciones que tiene almacenada
	 * el servidor
	 * @return Array de Strings o null en caso de error
	 * 
	 * @exception IOException, ClassNotFoundException
	 */
	public String[] recibirLista() {
		try {
			ObjectInputStream ois = new ObjectInputStream(is);
			String[] lista = (String[]) ois.readObject();
			return lista;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Lee una cancion por teclado y la manda al servidor
	 * como cancion pedida.
	 * @return Devuelve la cancion introducida por teclado
	 */
	public void pedirCancion(String cancionPedida) {
		PrintWriter pw = new PrintWriter(os);
		pw.println(cancionPedida);
		pw.flush();

	}


	
	/**
	 * Si se ha establecido correctamente una conexion con el servidor,
	 * al invocar a este metodo se cierra dicha conexion.
	 * 
	 * @exception IOException
	 */
	public void desconectar() {
		if (cliente != null)
			try {
				System.out.println("Te has desconectado, hasta la proxima.");
				cliente.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	
	/**
	 * Crea un hilo en el que se reproduce la cancion. Como parametros se le envia la cancion que actualmente esta sonando 
	 * y la cancion que va a sonar posteriormente. Si la cancion que se reproduce es distinta a la que se
	 * quiere reproducir, la posicion desde donde reproducir es 0.
	 * 
	 * Si el parametro inicio esta en true, significa que la cancion que esta sonando quieres que se reproduzca desde 
	 * el inicio.
	 * 
	 * @throws UnsupportedAudioFileException, IOException, LineUnavailableException
	 */
	public void reproducirWAV(String sonando, String cancionVieja, boolean inicio) {
		is = new BufferedInputStream(is);
		AudioInputStream ais;
		try {
			ais = AudioSystem.getAudioInputStream(is);
			this.clip = AudioSystem.getClip();
			clip.open(ais);		
			if((cancionVieja!=null && sonando.compareTo(cancionVieja)!=0)) {
				poscionPause=0;
			}
			
			if(inicio) {
				poscionPause = 0;
			}
			hilo = new Playing(clip, poscionPause, sonando);
			hilo.start();
			
	
			
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	/**
	 * Pausa la cancion y guarda en poscionPause el tiempo que lleva 
	 * sonando la cancion.
	 * 
	 * @param cancion
	 */
	public void pausarWAV(String cancion) {
		if(hilo != null) {
			poscionPause = hilo.parar(cancion);
			
		}
	}
	
	/**
	 * Comprueba que la cancion ha acabado
	 * 
	 * @return true si ha acabado, y falso en caso contrario
	 */
	public boolean acabado() {
		return hilo.acabado();
	}
	
	/**
	 * Establece la posicion del audio del hilo reproductor.
	 * 
	 * @param t valor desde donde quieres reproducir la cancion
	 */
	public void setTiempo(int t)
	{
		hilo.setTiempo(t);
	}

	
}
