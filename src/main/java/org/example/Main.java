package org.example;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        while (continuar) {
            System.out.println("Insira um URL válido:");
            String url = scanner.nextLine();

            if (!isUrlValida(url)) {
                System.out.println("URL inválido. Por favor, insira um URL válido.");
                continue;
            }

            System.out.println("Insira uma frase para procurar:");
            String frase = scanner.nextLine();

            try {
                // Faz a requisição HTTP e processa o conteúdo
                String conteudoUrl = fazerRequisicao(url);

                // Processa e obtem o resultado das ocorrências
                String resultado = processaOcorrencias(conteudoUrl, frase);
                System.out.println(resultado);

            } catch (IOException e) {
                System.out.println("Erro ao acessar o URL. Por favor, verifique a URL e tente novamente.");
            }

            // Pergunta ao usuário se deseja continuar
            System.out.println("Deseja realizar outro teste? (s/n)");
            String resposta = scanner.nextLine().trim().toLowerCase();

            if (!resposta.equals("s")) {
                continuar = false;
            }
        }

        scanner.close();
    }

    private static String fazerRequisicao(String url) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            String html = EntityUtils.toString(entity);

            Document document = Jsoup.parse(html);
            return document.text();
        }
    }

    private static boolean isUrlValida(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }

    private static String processaOcorrencias(String conteudoUrl, String frase) {
        StringBuilder resultado = new StringBuilder();

        // Conta as ocorrências da frase
        int contagemFrase = contarOcorrencias(conteudoUrl, frase, true);
        resultado.append("\"").append(frase).append("\" ⇒ repete ").append(contagemFrase).append(" vezes\n");

        // Conta as ocorrências de cada palavra
        String[] palavras = frase.split("\\s+");
        for (String palavra : palavras) {
            int contagemPalavra = contarOcorrencias(conteudoUrl, palavra, false);
            resultado.append("\"").append(palavra).append("\" ⇒ repete ").append(contagemPalavra).append(" vezes\n");
        }

        return resultado.toString();
    }

    private static int contarOcorrencias(String texto, String termo, boolean buscaExata) {
        int contador = 0;
        String padrao;

        if (buscaExata) {
            // Buscar a frase exata
            padrao = Pattern.quote(termo);
        } else {
            // Buscar palavra e delimitada por espaços ou pontuação
            padrao = "\\b" + Pattern.quote(termo) + "\\b";
        }

        Pattern pattern = Pattern.compile(padrao, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(texto);

        while (matcher.find()) {
            contador++;
        }

        return contador;
    }
}