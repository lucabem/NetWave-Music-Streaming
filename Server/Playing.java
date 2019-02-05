package Server;

import javax.sound.sampled.Clip;

public class Playing extends Thread {

	private Clip clip;
	private long t ;
	private String cancionPausada, cancionPlay;
	
	/**
	 * Constructor: inicializa las variables
	 * @param c : se encarga de reproducir la cancion
	 * @param pos : desde donde se reproducira la cancion
	 * @param sonando : cancion que esta sonando
	 */
	public Playing (Clip c, long pos, String sonando) {
		this.clip = c;
		t = pos;
		cancionPlay = sonando;
	}
		
	/**
	 * Establece el inicio del clip en la variable t e inicia la reproduccion.
	 * Este metodo es bloqueante (clip.drain()) hasta que no se acaba la reproduccion del clip.
	 */
	@Override
	public void run() {

		clip.setMicrosecondPosition(t);
		clip.start();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		clip.drain();
	}
	
	/**
	 * 
	 * @param cancion cancion que esta sonando
	 * @return tiempo que lleva reproduciendose la cancion en milisegundos
	 */
	public long parar(String cancion) {
		cancionPausada = cancion;
		t = clip.getMicrosecondPosition();
		clip.stop();
		return t;
	}

	public boolean acabado() {
		// TODO Auto-generated method stub
		return clip.getMicrosecondPosition() == clip.getMicrosecondLength();
	}
	
	public void setTiempo(int t) {
		this.t = t;
	}
	
}
