package ArnoldsCatMapMate_OrtizLopezFrieda;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class ArnoldsCatMapMate_OrtizLopezFrieda {
    public static void main(String[] args) throws Exception {
        //System.out.println("Working dir: " + System.getProperty("user.dir")); //usé esta linea para ver en qué directorio está la imagen
        String rutaEntrada = "src/ArnoldsCatMapMate_OrtizLopezFrieda/prueba.jpg";

        //se carga la imagen original
        BufferedImage imgOriginal = ImageIO.read(new File(rutaEntrada));
        int tam = imgOriginal.getWidth();
        if (imgOriginal.getHeight() != tam) {
            throw new Exception("La imagen debe ser cuadrada (NxN pixeles)");
        }

        //se convierte a matriz de pixeles
        int[][] original = new int[tam][tam];
        for (int x = 0; x < tam; x++) {
            for (int y = 0; y < tam; y++) {
                original[x][y] = imgOriginal.getRGB(x, y);
            }
        }

        //la primera iteracion es la imagen transformada
        int[][] transformada = aplicarMapa(original, tam);
        guardarImagen(transformada, tam, "transformada.png");

        //calcula la correlacion entre la imagen original y la transformada
        double corrTransformada = correlacion(original, transformada, tam);

        //encuentra el periodo para recuperar la original
        int[][] temporal = original;
        int iteraciones = 0;
        do {
            temporal = aplicarMapa(temporal, tam);
            iteraciones++;
        } while (!igualMatrices(temporal, original, tam));
        guardarImagen(temporal, tam, "recuperada.png");

        // se guarda la imagen original
        guardarImagen(original, tam, "original.png");

        //se guarda la imagen con menor correlación (misma transformada)
        guardarImagen(transformada, tam, "correlacion_menor.png");

        //imprimí resultados en la consola 
        System.out.println("\nRESULTADO DE LA TRANSFORMACION CON ARNOLD'S CAT MAP\n");
        System.out.println("Encontrado periodo: " + iteraciones + " iteraciones\n");
        System.out.println("Imagen original: original.png");
        System.out.println("Imagen transformada: transformada.png");
        System.out.println("Imagen recuperada despues de " + iteraciones + " iteraciones: recuperada.png");
        System.out.printf("Imagen con menor correlacion (%.6f): correlacion_menor.png\n", corrTransformada);
        if (igualMatrices(temporal, original, tam)) {
            System.out.println("Las imagenes son identicas");
        } else {
            System.out.println("Las imagenes NO son identicas");
        }
    }

    // Una iteración del Arnold Cat Map
    static int[][] aplicarMapa(int[][] entrada, int tamaño) {
        int[][] salida = new int[tamaño][tamaño];
        for (int x = 0; x < tamaño; x++) {
            for (int y = 0; y < tamaño; y++) {
                int nuevoX = (x + y) % tamaño;
                int nuevoY = (x + 2 * y) % tamaño;
                salida[nuevoX][nuevoY] = entrada[x][y];
            }
        }
        return salida;
    }

    // Compara dos matrices de píxeles
    static boolean igualMatrices(int[][] a, int[][] b, int tamaño) {
        for (int i = 0; i < tamaño; i++) {
            for (int j = 0; j < tamaño; j++) {
                if (a[i][j] != b[i][j]) return false;
            }
        }
        return true;
    }

    // Guarda una matriz de píxeles como imagen PNG
    static void guardarImagen(int[][] pixeles, int tamaño, String nombreArchivo) throws Exception {
        BufferedImage salida = new BufferedImage(tamaño, tamaño, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < tamaño; x++) {
            for (int y = 0; y < tamaño; y++) {
                salida.setRGB(x, y, pixeles[x][y]);
            }
        }
        ImageIO.write(salida, "png", new File(nombreArchivo));
    }

    // Calcula la correlación de Pearson entre dos matrices (en escala de grises)
    static double correlacion(int[][] img1, int[][] img2, int tamaño) {
        int n = tamaño * tamaño;
        double[] a = new double[n];
        double[] b = new double[n];
        int idx = 0;
        for (int x = 0; x < tamaño; x++) {
            for (int y = 0; y < tamaño; y++) {
                int rgb1 = img1[x][y];
                int rgb2 = img2[x][y];
                int g1 = (((rgb1>>16)&0xFF) + ((rgb1>>8)&0xFF) + (rgb1&0xFF)) / 3;
                int g2 = (((rgb2>>16)&0xFF) + ((rgb2>>8)&0xFF) + (rgb2&0xFF)) / 3;
                a[idx] = g1;
                b[idx] = g2;
                idx++;
            }
        }
        double meanA = media(a);
        double meanB = media(b);
        double cov = 0, varA = 0, varB = 0;
        for (int i = 0; i < n; i++) {
            cov += (a[i] - meanA) * (b[i] - meanB);
            varA += Math.pow(a[i] - meanA, 2);
            varB += Math.pow(b[i] - meanB, 2);
        }
        return cov / Math.sqrt(varA * varB);
    }

    // Media aritmética simple
    static double media(double[] arr) {
        double suma = 0;
        for (int i = 0; i < arr.length; i++) {
            suma = suma + arr[i];
        }
        double promedio = suma / arr.length;
        return promedio;
    }
}