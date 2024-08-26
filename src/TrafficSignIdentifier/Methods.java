
package TrafficSignIdentifier;

import java.awt.image.BufferedImage;


public interface Methods {

    public BufferedImage filterWarna(BufferedImage image);

    public static BufferedImage toGrayScale(BufferedImage image) {
        BufferedImage grayImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        int rgb = 0, r = 0, g = 0, b = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                rgb = (int) (image.getRGB(x, y));
                r = ((rgb >> 16) & 0xFF);
                g = ((rgb >> 8) & 0xFF);
                b = (rgb & 0xFF);
                rgb = (int) ((r + g + b) / 3);
                rgb = (255 << 24) | (rgb << 16) | (rgb << 8) | rgb;
                grayImage.setRGB(x, y, rgb);
            }

        }
        return grayImage;
    }

    public static BufferedImage detectEdges(BufferedImage img) {
        int h = img.getHeight(), w = img.getWidth(), threshold = 20, p = 0;
        BufferedImage edgeImg = toGrayScale(new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY));
        int[][] vert = new int[w][h];
        int[][] horiz = new int[w][h];
        int[][] edgeWeight = new int[w][h];
        for (int y = 1; y < h - 1; y++) {
            for (int x = 1; x < w - 1; x++) {
                vert[x][y] = (int) (img.getRGB(x + 1, y - 1) & 0xFF + 2 * (img.getRGB(x + 1, y) & 0xFF) + img.getRGB(x + 1, y + 1) & 0xFF
                        - img.getRGB(x - 1, y - 1) & 0xFF - 2 * (img.getRGB(x - 1, y) & 0xFF) - img.getRGB(x - 1, y + 1) & 0xFF);
                horiz[x][y] = (int) (img.getRGB(x - 1, y + 1) & 0xFF + 2 * (img.getRGB(x, y + 1) & 0xFF) + img.getRGB(x + 1, y + 1) & 0xFF
                        - img.getRGB(x - 1, y - 1) & 0xFF - 2 * (img.getRGB(x, y - 1) & 0xFF) - img.getRGB(x + 1, y - 1) & 0xFF);
                edgeWeight[x][y] = (int) (Math.sqrt(vert[x][y] * vert[x][y] + horiz[x][y] * horiz[x][y]));
                if (edgeWeight[x][y] > threshold) {
                    p = (255 << 24) | (255 << 16) | (255 << 8) | 255;
                } else {
                    p = (255 << 24) | (0 << 16) | (0 << 8) | 0;
                }
                edgeImg.setRGB(x, y, p);
            }
        }
        return edgeImg;
    }

    public static String detectShape(BufferedImage img) {
        int tepiPanjang = 0; // Menandakan tidak ada tepi panjang ditemukan
        int tepiTinggi = 0; // Menandakan tidak ada tepi tinggi ditemukan
        int y = img.getHeight(), x = img.getWidth();

        // Vertical (tepi panjang)
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                int rgb = img.getRGB(i, j);
                if ((rgb & 0x00FFFFFF) != 0) { // Memeriksa apakah warna tidak transparan
                    tepiPanjang++;
                    break; // Keluar dari loop dalam (j) jika menemukan tepi
                }
            }
        }

        // Horizontal (tepi tinggi)
        for (int j = 0; j < y; j++) {
            for (int i = 0; i < x; i++) {
                int rgb = img.getRGB(i, j);
                if ((rgb & 0x00FFFFFF) != 0) { // Memeriksa apakah warna tidak transparan
                    tepiTinggi++;
                    break; // Keluar dari loop dalam (i) jika menemukan tepi
                }
            }
        }

        int selisih = tepiPanjang - tepiTinggi;

        if (selisih >= 20 && selisih <= 40) {
            return "SEGITIGA, selisih: " + selisih + ", Tepi panjang: " + tepiPanjang + ", Tepi samping: " + tepiTinggi;
        } else if (selisih >= 1 && selisih <= 10) {
            return "PERSEGI, selisih: " + selisih + ", Tepi panjang: " + tepiPanjang + ", Tepi samping: " + tepiTinggi;
        } else if (Math.abs(selisih) >= 40 && Math.abs(selisih) <= 250) {
            return "PERSEGI PANJANG, selisih: " + selisih + ", Tepi panjang: " + tepiPanjang + " Tepi samping: " + tepiTinggi;
        } else if (selisih == 0) {
            return "LINGKARAN, selisih: " + selisih + ", Tepi panjang: " + tepiPanjang + " tepi samping: " + tepiTinggi;
        } else if (selisih >= 0 && selisih <= 5) {
            return "LINGKARAN, selisih: " + selisih + ", Tepi panjang: " + tepiPanjang + " tepi samping: " + tepiTinggi;
        } else if (selisih <= 2) {
            return "BELAH KETUPAT, selisih: " + selisih + ", Tepi panjang: " + tepiPanjang + ", Tepi samping: " + tepiTinggi;
        } else {
            return "LINGKARAN, selisih: " + selisih + ", Tepi panjang: " + tepiPanjang + " tepi samping: " + tepiTinggi;
        }
    }
 
    public String detectSign();
}
