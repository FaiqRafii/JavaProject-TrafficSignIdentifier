package TrafficSignIdentifier;

import java.awt.image.BufferedImage;

public class ImageProcessing extends RambuLaluLintas implements Methods {

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

    public BufferedImage filterWarna(BufferedImage image) {
        BufferedImage identifiedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        int Threshold = 100;
        int redThreshold = 150;
        int countKuning = 0, countBiru = 0, countMerah = 0, countHijau = 0;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                // Kuning
                if (r > Threshold && g > Threshold && b < Threshold) {
                    identifiedImage.setRGB(x, y, rgb);
                    countKuning++;

                    // Biru
                } else if (b > Threshold && r < Threshold && g < Threshold) {
                    identifiedImage.setRGB(x, y, rgb);
//                    System.out.println(identifiedImage.getRGB(x, y));
                    countBiru++;

                    // Merah
                } else if (r > redThreshold && g < redThreshold && b < redThreshold) {
                    identifiedImage.setRGB(x, y, rgb);

//                    System.out.println(identifiedImage.getRGB(x, y));
                    countMerah++;

                    // Hijau
                } else if (g > Threshold && r < Threshold && b < Threshold) {
                    identifiedImage.setRGB(x, y, rgb);
//                    System.out.println(identifiedImage.getRGB(x, y));
                    countHijau++;

                } else if (r == 0 && g == 0 && b == 0) {
                    identifiedImage.setRGB(x, y, rgb);
//                    System.out.println(identifiedImage.getRGB(x, y));
                } else {
                    identifiedImage.setRGB(x, y, 0xFFFFFF);
//                    identifiedImage.setRGB(x, y, rgb);
//                    System.out.println(identifiedImage.getRGB(x, y));
                }
            }

        }

        int[] warna = {countMerah, countKuning, countBiru, countHijau};
        String[] namaWarna = {"Merah", "Kuning", "Biru", "Hijau"};
        String[] jenisRambu = {"Larangan", "Peringatan", "Biru", "Hijau"};
        int maxCount = 0;

        for (int i = 0; i < warna.length; i++) {
            if (warna[i] > maxCount) {
                maxCount = warna[i];
                this.warnaTerbanyak = namaWarna[i];
            }
        }

        switch (this.warnaTerbanyak) {
            case "Merah":
                setHasilWarna("Merah");
                break;
            case "Biru":
                setHasilWarna("Biru");
                break;
            case "Hijau":
                setHasilWarna("Hijau");
                break;
            case "Kuning":
                setHasilWarna("Kuning");
        }

        System.out.println("Warna terbanyak: " + this.warnaTerbanyak + "\nJumlah piksel: " + maxCount);
        System.out.println("=============================\nMerah: " + countMerah);
        System.out.println("Kuning: " + countKuning);
        System.out.println("Biru: " + countBiru);
        System.out.println("Hijau: " + countHijau + "\n");
        return identifiedImage;
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
        int tepiLebar = 0; 
        int tepiTinggi = 0; 
        int y = img.getHeight(), x = img.getWidth();

        // Vertical (tepi lebar)
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                int rgb = img.getRGB(i, j);
                if ((rgb & 0x00FFFFFF) != 0) { 
                    tepiLebar++;
                    break; 
                }
            }
        }

        // Horizontal (tepi tinggi)
        for (int j = 0; j < y; j++) {
            for (int i = 0; i < x; i++) {
                int rgb = img.getRGB(i, j);
                if ((rgb & 0x00FFFFFF) != 0) { 
                    tepiTinggi++;
                    break; 
                }
            }
        }

        int selisih = tepiLebar - tepiTinggi;

        if (selisih >= 20 && selisih <= 40) {
            return "SEGITIGA, selisih: " + selisih + ", Tepi panjang: " + tepiLebar + ", Tepi samping: " + tepiTinggi;
        } else if (selisih >= 1 && selisih <= 10) {
            return "PERSEGI, selisih: " + selisih + ", Tepi panjang: " + tepiLebar + ", Tepi samping: " + tepiTinggi;
        } else if (Math.abs(selisih) >= 40 && Math.abs(selisih) <= 250) {
            return "PERSEGI PANJANG, selisih: " + selisih + ", Tepi panjang: " + tepiLebar + " Tepi samping: " + tepiTinggi;
        } else if (selisih == 0) {
            return "LINGKARAN";
        } else if (selisih >= 0 && selisih <= 5) {
            return "LINGKARAN";
        } else if (selisih <= 2) {
            return "BELAH KETUPAT, selisih: " + selisih + ", Tepi panjang: " + tepiLebar + ", Tepi samping: " + tepiTinggi;
        } else {
            return "LINGKARAN, selisih: " + selisih + ", Tepi panjang: " + tepiLebar + " tepi samping: " + tepiTinggi;
        }
    }

public String detectSign() {
    String rambu = "";
    switch (this.warnaTerbanyak) {
        case "Merah":
            rambu = "Larangan";
            break;
        case "Biru":
            if (getHasilBentuk().equalsIgnoreCase("Lingkaran")) {
                rambu = "Perintah";
            } else {
                rambu = "Petunjuk";
            }
            break;
        case "Hijau":
            rambu = "Petunjuk";
            break;
        case "Kuning":
            rambu = "Peringatan";
            break;
        default:
            rambu = "Tidak Diketahui"; // Menangani kasus tak terduga
            break;
    }
    return rambu;
}

}
