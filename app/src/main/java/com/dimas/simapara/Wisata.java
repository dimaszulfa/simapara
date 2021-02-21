package com.dimas.simapara;

public class Wisata {
    private String WisataName;
    private String WisataDes;
    private String ImageUrl;
    private String Provinsi,Kota,Kecamatan,Kelurahan;

    public Wisata(String wisataName, String wisataDes, String imageUrl, String provinsi, String kota, String kecamatan, String kelurahan) {
        WisataName = wisataName;
        WisataDes = wisataDes;
        ImageUrl = imageUrl;
        Provinsi = provinsi;
        Kota = kota;
        Kecamatan = kecamatan;
        Kelurahan = kelurahan;
    }

    public Wisata() {

    }

    public String getWisataName() {
        return WisataName;
    }
    public String getProvinsi() {
        return Provinsi;
    }

    public String getKota() {
        return Kota;
    }

    public String getKecamatan() {
        return Kecamatan;
    }
    public String getKelurahan() {
        return Kecamatan;
    }
    public void setWisataName(String wisataName) {
        WisataName = wisataName;
    }

    public String getWisataDes() {
        return WisataDes;
    }

    public void setWisataDes(String wisataDes) {
        WisataDes = wisataDes;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
}
