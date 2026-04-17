import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

class Carta {
    private String naipe;
    private String valor;

    public Carta(String naipe, String valor) {
        this.naipe = naipe;
        this.valor = valor;
    }

    public int getPontos() {
        switch (valor) {
            case "A": return 11;
            case "J":
            case "Q":
            case "K": return 10;
            default: return Integer.parseInt(valor);
        }
    }

    public String getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return valor + " de " + naipe;
    }
}

class Baralho {
    private List<Carta> cartas;

    public Baralho() {
        String[] naipes = {"Copas", "Ouros", "Paus", "Espadas"};
        String[] valores = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        cartas = new ArrayList<>();
        for (String naipe : naipes) {
            for (String valor : valores) {
                cartas.add(new Carta(naipe, valor));
            }
        }
    }

    public void embaralhar() {
        Collections.shuffle(cartas);
    }

    public Carta puxarCarta() {
        if (cartas.isEmpty()) return null;
        return cartas.remove(0);
    }
}

class Jogador {
    private String nome;
    private List<Carta> mao;
    private boolean ehDealer;

    public Jogador(String nome, boolean ehDealer) {
        this.nome = nome;
        this.mao = new ArrayList<>();
        this.ehDealer = ehDealer;
    }

    public void receberCarta(Carta carta) {
        if (carta!= null) mao.add(carta);
    }

    public int calcularPontos() {
        int pontos = 0;
        int qtdAs = 0;

        for (Carta carta : mao) {
            pontos += carta.getPontos();
            if (carta.getValor().equals("A")) qtdAs++;
        }

        while (pontos > 21 && qtdAs > 0) {
            pontos -= 10;
            qtdAs--;
        }
        return pontos;
    }

    public String statusPontuacao() {
        int pontos = calcularPontos();
        if (pontos > 21) return "acima de 21 - estourou!";
        else if (pontos == 21) return "igual a 21 - Blackjack!";
        else return "abaixo de 21";
    }

    public void mostrarMao(boolean esconderPrimeira) {
        System.out.print(nome + " - Mão: ");
        for (int i = 0; i < mao.size(); i++) {
            if (esconderPrimeira && i == 0 && ehDealer) {
                System.out.print("[CARTA OCULTA]");
            } else {
                System.out.print(mao.get(i));
            }
            if (i < mao.size() - 1) System.out.print(", ");
        }
        if (!esconderPrimeira ||!ehDealer) {
            System.out.print(" | Pontos: " + calcularPontos());
        }
        System.out.println();
    }

    public boolean deveContinuarComprando() {
        return ehDealer && calcularPontos() < 17;
    }

    public boolean estourou() {
        return calcularPontos() > 21;
    }

    public String getNome() {
        return nome;
    }

    public void limparMao() {
        mao.clear();
    }
}

public class Blackjack {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Baralho baralho = new Baralho();
        Jogador jogador = new Jogador("Fernando", false);
        Jogador dealer = new Jogador("Dealer", true);

        String jogarNovamente;

        do {
            baralho = new Baralho(); // Baralho novo a cada rodada
            baralho.embaralhar();
            jogador.limparMao();
            dealer.limparMao();

            // Distribuição inicial
            jogador.receberCarta(baralho.puxarCarta());
            dealer.receberCarta(baralho.puxarCarta());
            jogador.receberCarta(baralho.puxarCarta());
            dealer.receberCarta(baralho.puxarCarta());

            System.out.println("\n=== NOVA RODADA ===");
            dealer.mostrarMao(true); // Esconde primeira carta do dealer
            jogador.mostrarMao(false);

            // Turno do jogador
            String opcao = "";
            while (!jogador.estourou() &&!opcao.equals("P")) {
                System.out.print("\n[C] Comprar carta | [P] Parar: ");
                opcao = sc.next().toUpperCase();

                if (opcao.equals("C")) {
                    jogador.receberCarta(baralho.puxarCarta());
                    System.out.println("Você comprou uma carta!");
                    jogador.mostrarMao(false);
                    System.out.println("Status: " + jogador.statusPontuacao());
                }
            }

            // Turno do dealer se jogador não estourou
            if (!jogador.estourou()) {
                System.out.println("\n--- Turno do Dealer ---");
                dealer.mostrarMao(false);

                while (dealer.deveContinuarComprando()) {
                    System.out.println("Dealer compra...");
                    dealer.receberCarta(baralho.puxarCarta());
                    dealer.mostrarMao(false);
                }
            }

            // Resultado
            System.out.println("\n=== RESULTADO FINAL ===");
            jogador.mostrarMao(false);
            dealer.mostrarMao(false);

            int pontosJogador = jogador.calcularPontos();
            int pontosDealer = dealer.calcularPontos();

            if (jogador.estourou()) {
                System.out.println("Você estourou! Dealer vence.");
            } else if (dealer.estourou()) {
                System.out.println("Dealer estourou! Você vence!");
            } else if (pontosJogador > pontosDealer) {
                System.out.println("Você vence com " + pontosJogador + " contra " + pontosDealer + "!");
            } else if (pontosDealer > pontosJogador) {
                System.out.println("Dealer vence com " + pontosDealer + " contra " + pontosJogador + ".");
            } else {
                System.out.println("Empate! Ambos com " + pontosJogador + " pontos.");
            }

            System.out.print("\nJogar novamente? [S/N]: ");
            jogarNovamente = sc.next().toUpperCase();

        } while (jogarNovamente.equals("S"));

        System.out.println("Valeu pelo jogo!");
        sc.close();
    }
}
