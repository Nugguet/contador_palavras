package org.example;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.swing.JOptionPane;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        boolean continuar = true;

        // função para receber link e frase do usuario
        while (continuar) {

            String url = JOptionPane.showInputDialog(null, "Insira um URL válido:", "Entrada de URL", JOptionPane.QUESTION_MESSAGE);

            if (url == null) {
                break;
            }

            if (!isUrlValida(url)) {
                JOptionPane.showMessageDialog(null, "URL inválido. Por favor, insira um URL válido.", "Erro", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            String frase = JOptionPane.showInputDialog(null, "Insira uma frase para procurar:", "Entrada de Frase", JOptionPane.QUESTION_MESSAGE);

            if (frase == null) {
                break;
            }

            try {
                // Faz a requisição HTTP e processa o conteúdo
                String conteudoUrl = fazerRequisicao(url);

                // Processa e obtem o resultado das ocorrências
                String resultado = processaOcorrencias(conteudoUrl, frase);

                JOptionPane.showMessageDialog(null, resultado, "Resultado", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Erro ao acessar o URL. Por favor, verifique a URL e tente novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
            }

            // Pergunta ao usuário se deseja continuar
            int escolha = JOptionPane.showOptionDialog(
                    null,
                    "Deseja realizar outro teste?",
                    "Continuar?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[]{"Sim", "Não"},
                    "Sim"
            );

            // Finaliza o loop
            if (escolha == JOptionPane.NO_OPTION) {
                continuar = false;
            }
        }
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

        Pattern pattern = Pattern.compile(padrao);
        Matcher matcher = pattern.matcher(texto);

        while (matcher.find()) {
            contador++;
        }

        return contador;
    }
}