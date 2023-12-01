package org.example;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // Désactiver la vérification SSL
        System.setProperty("jsse.enableSNIExtension", "false");
        System.setProperty("https.protocols", "TLSv1.2,TLSv1.1,SSLv3");

        Scanner scanner = new Scanner(System.in);
        System.out.print("Veuillez saisir un lien : ");
        String urlWebsite = scanner.nextLine();
        System.out.println("Vous avez saisi le lien : " + urlWebsite);

        System.out.print("Veuillez saisir le mot particulier : ");
        String motParticulier = scanner.nextLine().toLowerCase(); // Convertir en minuscules pour la comparaison
        scanner.close();

        try {
            Document document = Jsoup.connect(urlWebsite).get();

            Elements liens = document.select("a[href]");

            System.out.println("Liens extraits de la page contenant le texte particulier :");
            for (Element lienElement : liens) {
                String texteLien = lienElement.text().toLowerCase();
                if (texteLien.contains(motParticulier)) {
                    System.out.println(lienElement.attr("href"));
                }
            }

            String cheminPDF = "C:\\Users\\r_boug\\Desktop\\scrapping.pdf";

            int i = 1;
            while (new File(cheminPDF).exists()) {
                cheminPDF = "C:\\Users\\r_boug\\Desktop\\scrapping" + i + ".pdf";
                i++;
            }

            genererPDF(liens, cheminPDF, motParticulier);

        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }
    }

    private static void genererPDF(Elements liens, String nomFichier, String motParticulier) throws DocumentException {
        com.itextpdf.text.Document document = new com.itextpdf.text.Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(nomFichier));
            document.open();

            boolean desLiensAjoutes = false;

            for (Element lienElement : liens) {
                String texteLien = lienElement.text().toLowerCase();
                if (texteLien.contains(motParticulier)) {
                    document.add(new Paragraph(lienElement.attr("href")));
                    desLiensAjoutes = true;
                }
            }

            // Ajouter une page seulement si des liens ont été ajoutés
            if (!desLiensAjoutes) {
                document.add(new Paragraph("Aucun lien correspondant au mot particulier trouvé."));
            }

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        } finally {
            if (document != null && document.isOpen()) {
                document.close();
            }
        }
    }
}