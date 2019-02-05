package Servidor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MusicServerHilo implements Runnable {

	private Socket conexion;
	private int id;
	private File directorio;
	private OutputStream out;
	private InputStream in;
	private BufferedReader br;

	/**
	 * Inicia las variables y  obtiene el inputStream y outputStream de la conexion.
	 * @param conexion cliente
	 * @param id identificador del hilo
	 * @param f  directorio de las canciones
	 */
	public MusicServerHilo(Socket conexion, int id, File f) {
		this.conexion = conexion;
		this.id = id;
		this.directorio = f;
		
		if (this.conexion != null) {
			try {
				out = this.conexion.getOutputStream();
				in = this.conexion.getInputStream();
				br = new BufferedReader(new InputStreamReader(in));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Manda la lista de canciones al cliente y se queda esperando a que éste le mande la cancion seleccionada
	 * Una vez le envia la cancion, abre el fichero y se lo manda por bloques.
	 * 
	 * Una vez mandado el fichero, se queda esperando a la peticion de otra cancion.
	 */
	public void run() {
		try {
			mandarListaCanciones(directorio, this.conexion);

			String cancionPedida = "";

			FileInputStream fis = null;
			
			cancionPedida = recibirCancion();
			while (cancionPedida != null && cancionPedida.compareTo("Exit") != 0) {
				File cancion = new File(directorio.getAbsolutePath() + "\\" + cancionPedida + ".wav");

				fis = new FileInputStream(cancion);

				byte buffer[] = new byte[1024];
				int count;
				while ((count = fis.read(buffer)) != -1)
					out.write(buffer, 0, count);

				out.flush();

				cancionPedida = recibirCancion();
			}

			if (fis != null)
				fis.close();
			if (out != null)
				out.close();
			
			this.desconectar();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Serializa el listado de las canciones que contiene el servidor, y las envia al cliente
	 * @param d : directorio de las canciones
	 * @param cliente: conexion con el cliente
	 */
	private void mandarListaCanciones(File d, Socket cliente) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(out);
			oos.writeObject(d.list());
			oos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Lee la cancion pedida por el cliente a traves del InputStream del servidor.
	 * @return String 
	 */
	private String recibirCancion() {
		try {
			return br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Exit";
		}
	}

	/**
	 * El cliente se desconecta del servidor, y el hilo deja de 
	 * mantener una conexion.
	 */
	public void desconectar() {
		try {
			if(conexion != null){
				conexion.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
