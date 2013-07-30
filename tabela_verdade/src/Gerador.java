package tabela_verdade;

import arquivo.Arquivo;

/**
 * @author CaioCalado {cfpc@cin.ufpe.br}
 */
public class Gerador {

  static Atomico[] valores;
	static int indice_valores;
	static int indice;

	public static void main(String[] args) {
		Arquivo io = new Arquivo("Expressoes.in", "Expressoes.out");
		iniciar();
		lerArquivo(io);
		io.close();

	}

	public static void lerArquivo(Arquivo io) {
		String exp_aux = "";
		while (!io.isEndOfFile()) {
			int counter = io.readInt();
			for (int i = 1; i <= counter; i++) {
				exp_aux = io.readString();
				iniciar();
				ler_expressao(exp_aux);
				retira_duplicado();
				ordena();
				io.println("Tabela #" + i);
				manipular_expressao(io);

			}
		}
	}

	public static void finaliza_expressao(Arquivo io) {
		int i = indice_valores - 1; // onde tá a conclusão

		int tam = valores[i].op.length;
		boolean tau = true;
		boolean sat = false;

		for (int j = 0; j < tam; j++) {
			if (valores[i].get(j)) {
				sat = true;
			} else {
				tau = false;
			}
		}
		String res = "";
		if (sat) {
			res = "satisfativel e ";
		} else {
			res = "insatisfativel e ";
		}

		if (tau) {
			res += "tautologia";
		} else {
			res += "refutavel";
		}
		io.println(res);
	}

	public static void manipular_expressao(Arquivo io) {
		int qtd = qtd_atomicas();
		if (qtd > 0) {
			atualizar_atomicas(qtd);
			imprimir_valores((int) Math.pow(2, qtd), io);
			finaliza_expressao(io);
		}
		io.println();
	}

	public static void atualizar_atomicas(int qtd) {
		int counter = 0;
		int i = 0;
		int tam = (int) Math.pow(2, qtd);

		for (i = 0; i < indice_valores; i++)
			valores[i].create(tam);

		i = 0;
		// inicializando os valores ATÔMICOS
		while (i < indice_valores && counter < qtd) {

			if (valores[i].atomica && !valores[i].constante) {
				int sentinela = (int) Math.pow(2, (qtd) - (counter + 1));
				int index = 0;
				boolean value = false;

				for (int j = 0; j < tam; j++) {
					if (index < (sentinela)) {
						valores[i].set(j, value);
						index++;
					} else {
						value = !value;
						valores[i].set(j, value);
						index = 1;
					}
				}
				counter++;
			}
			i++;
		}// end while
		i = 0;
		counter = 0;
		// inicializando as constantes caso exista..
		while (i < indice_valores && counter < 2) {
			if (valores[i].constante && valores[i].atomica) {
				if (valores[i].exp.equals("" + 0)) {
					for (int j = 0; j < tam; j++) {
						valores[i].set(j, false);
					}
				} else {
					for (int j = 0; j < tam; j++) {
						valores[i].set(j, true);
					}
				}
				counter++;
			}
			i++;
		}

		// inicializando as expressoes caso exista..
		i = 0;
		int a = 0;
		int b = 0;
		while (i < indice_valores) {
			if (!valores[i].atomica) {
				switch (valores[i].operador) {
				case '-':
					a = get_position(valores[i].a);
					for (int j = 0; j < tam; j++) {
						valores[i].set(j, !valores[a].get(j));
					}
					break;
				case '.':
					a = get_position(valores[i].a);
					b = get_position(valores[i].b);
					for (int j = 0; j < tam; j++) {
						valores[i].set(j,
								valores[a].get(j) && valores[b].get(j));
					}
					break;
				case '>':
					a = get_position(valores[i].a);
					b = get_position(valores[i].b);
					for (int j = 0; j < tam; j++) {
						valores[i].set(j,
								!valores[a].get(j) || valores[b].get(j));
					}
					break;
				case '+':
					a = get_position(valores[i].a);
					b = get_position(valores[i].b);
					for (int j = 0; j < tam; j++) {
						valores[i].set(j,
								valores[a].get(j) || valores[b].get(j));
					}
					break;
				}

			}
			i++;
		}

	}

	public static int get_position(String exp) {
		int i = 0;

		while (i < indice_valores && !valores[i].exp.equals(exp)) {
			i++;
		}
		return i;
	}

	public static int qtd_atomicas() {
		int counter = 0;
		for (int i = 0; i < indice_valores; i++) {
			if (valores[i].atomica && !valores[i].constante) {
				counter++;
			}
		}

		return counter;
	}

	public static void iniciar() {
		valores = new Atomico[20];
		indice = 0;
		indice_valores = 0;
	}

	public static void expandir() {
		Atomico[] aux = new Atomico[valores.length * 2];
		for (int i = 0; i < indice_valores; i++) {
			aux[i] = valores[i];
		}

		valores = aux;
	}

	public static void imprimir_valores(int qtd, Arquivo io) {
		// metodo para imprimir o que tem dentro do array de atomicos
		// vertical print

		boolean f = true;
		int line = 0;
		String aux = "";
		String aux2 = "";
		for (int i = 0; i < indice_valores; i++) {
			if (f) {
				if (!valores[i].constante) {
					aux = "|" + valores[i].toString() + "|";
					aux2 += aux;
					line += aux.length();

					f = false;
				}
			} else {
				if (!valores[i].constante) {
					aux = valores[i].toString() + "|";
					aux2 += aux;
					line += aux.length();

					f = false;
				}
			}// end else
		}// end for

		aux = "";
		for (int i = 0; i < line; i++)
			aux += '-';
		io.println(aux + "\n" + aux2 + "\n" + aux);
		for (int j = 0; j < qtd; j++) {
			f = true;
			for (int i = 0; i < indice_valores; i++) {
				if (f) {
					if (!valores[i].constante) {
						io.print("|" + valores[i].toString_op(j) + "|");
						f = false;
					}
				} else {
					if (!valores[i].constante) {
						io.print(valores[i].toString_op(j) + "|");
						f = false;
					}
				}// end else
			}// end for
			io.println("\n" + aux);
		}// end FOR
	}

	public static void ler_expressao(String exp) {

		if (indice < exp.length()) {
			int key = 0;
			char c = exp.charAt(indice);
			switch (c) {
			case '(':
				indice++;
				ler_expressao(exp);
				break;
			case ')':
				return;
			case '-':
				indice++;
				ler_expressao(exp);
				if (indice_valores >= valores.length) {
					expandir();
				}
				valores[indice_valores] = new Atomico(
						valores[indice_valores - 1].exp, '-');
				indice_valores++;

				indice++;
				ler_expressao(exp);
				break;
			case '+':
				key = indice_valores - 1;
				indice++;
				ler_expressao(exp);

				if (indice_valores >= valores.length) {
					expandir();
				}
				// adicionando o valor com o último inserido
				valores[indice_valores] = new Atomico(valores[key].exp, '+',
						valores[indice_valores - 1].exp);
				indice_valores++;

				indice++;
				ler_expressao(exp);
				break;
			case '.':
				key = indice_valores - 1;
				indice++;
				ler_expressao(exp);

				if (indice_valores >= valores.length) {
					expandir();
				}
				// adicionando o valor com o último inserido
				valores[indice_valores] = new Atomico(valores[key].exp, '.',
						valores[indice_valores - 1].exp);
				indice_valores++;

				indice++;
				ler_expressao(exp);
				break;
			case '>':
				key = indice_valores - 1;
				indice++;
				ler_expressao(exp);

				if (indice_valores >= valores.length) {
					expandir();
				}
				// adicionando o valor com o último inserido
				valores[indice_valores] = new Atomico(valores[key].exp, '>',
						valores[indice_valores - 1].exp);
				indice_valores++;

				indice++;
				ler_expressao(exp);
				break;

			default:
				if (indice_valores >= valores.length) {
					expandir();
				}
				valores[indice_valores] = new Atomico(exp.charAt(indice) + "");
				indice_valores++;

				indice++;
				if (exp.charAt(indice) == ')') {
					return;
				} else {
					ler_expressao(exp);
				}
			}// fim switch

		}

	}// fim ler expressao

	public static void retira_duplicado() {
		Atomico[] aux = new Atomico[indice_valores];
		int index = 0;
		for (int i = 0; i < indice_valores; i++) {
			boolean e = false;
			for (int j = 0; j < i; j++) {
				if (valores[i].exp.equals(valores[j].exp)) {
					e = true;
				}
			}

			if (!e) {
				aux[index] = valores[i];
				index++;
			}
		}
		valores = aux;
		indice_valores = index;

	}

	public static void ordena() {
		int chave = 0;
		int i = 0;
		for (int j = 1; j < indice_valores; j++) {
			Atomico a = valores[j];
			chave = valores[j].peso;
			i = j - 1;

			while (i >= 0 && valores[i].peso > chave) {
				valores[i + 1] = valores[i];
				i -= 1;
			}
			valores[i + 1] = a;
		}

	}
}

class Atomico {
	boolean[] op;
	String exp;
	String a;
	String b;
	boolean atomica;
	boolean constante;
	char operador;
	int peso;

	public Atomico(String exp) {
		this.exp = exp;
		atomica = true;
		constante = is_constant(exp);
		get_peso();
	}

	// construtor para o tipo (-A)
	public Atomico(String exp, char operador) {
		this.a = exp;
		this.operador = '-';
		this.exp = "(-" + exp + ")";
		atomica = false;
		constante = is_constant(exp);
		get_peso();
	}

	// construtor para o tipo A X B
	// X = { + , . , > }
	public Atomico(String a, char operador, String b) {
		this.a = a;
		this.b = b;
		this.operador = operador;
		atomica = false;
		exp = "(" + a + operador + b + ")";
		constante = is_constant(exp);
		get_peso();
	}

	//it won't be a constant if the exp's values contains x | y | z | t
	private boolean is_constant(String exp) {
		return !(exp.contains("x") || exp.contains("y") || exp.contains("z")
				|| exp.contains("t"));

	}

	private void get_peso() {
		this.peso = 0;
		for (int i = 0; i < exp.length(); i++) {
			peso += exp.charAt(i);			
		}
		
		if (exp.equals("t") || exp.equals("(-t)")) 
			peso += 7;
		}
	

	public void create(int length) {
		op = new boolean[length];
	}

	// método para setar um valor da tabela verdade
	public void set(int i, boolean value) {
		op[i] = value;
	}

	// método para pegar um valor da tabela verdade
	public boolean get(int i) {
		return op[i];
	}

	public char get_operador() {
		return operador;
	}

	public String toString() {
		String aux = "";
		// aux = "[" + exp + "|" + peso + "]";
		aux = exp;
		return aux;
	}

	public String toString_op(int position) {
		String aux = "";

		for (int i = 0; i < exp.length() - 1; i++) {
			aux += " ";
		}
		if (op[position]) {
			aux += "1";
		} else {
			aux += "0";
		}
		return aux;
	}
}
