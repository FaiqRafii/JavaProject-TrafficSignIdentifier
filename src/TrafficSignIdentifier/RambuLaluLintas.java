package TrafficSignIdentifier;

import java.awt.image.BufferedImage;

public abstract class RambuLaluLintas implements Methods{

    public String warnaTerbanyak = "";
    private String hasilWarna,hasilBentuk,hasilRambu;

    public void setHasilWarna(String hasilWarna) {
        this.hasilWarna = hasilWarna;
    }

    public void setHasilBentuk(String hasilBentuk) {
        this.hasilBentuk = hasilBentuk;
    }

    public void setHasilRambu(String hasilRambu) {
        this.hasilRambu = hasilRambu;
    }

    public String getHasilWarna() {
        return hasilWarna;
    }

    public String getHasilBentuk() {
        return hasilBentuk;
    }

    public String getHasilRambu() {
        return hasilRambu;
    }
    
    
}
