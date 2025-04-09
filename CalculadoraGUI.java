import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Stack;
import java.lang.Math;

public class CalculadoraGUI extends JFrame {

    private JTextField display;
    private JPanel panelBotones;
    private boolean modoCientifico = false;

    public CalculadoraGUI() {
        setTitle("Calculadora Sabrosonga");
        setSize(400, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        display = new JTextField();
        display.setEditable(false);
        display.setFont(new Font("Consolas", Font.PLAIN, 24));
        add(display, BorderLayout.NORTH);

        panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(6, 4, 5, 5));
        add(panelBotones, BorderLayout.CENTER);

        crearBotones();

        JMenuBar barra = new JMenuBar();
        JMenu menu = new JMenu("Opciones");
        JMenuItem cambiarModo = new JMenuItem("Cambiar a modo científico");

        cambiarModo.addActionListener(e -> {
            modoCientifico = !modoCientifico;
            cambiarModo.setText(modoCientifico ? "Cambiar a modo básico" : "Cambiar a modo científico");
            panelBotones.removeAll();
            crearBotones();
            revalidate();
            repaint();
        });

        menu.add(cambiarModo);
        barra.add(menu);
        setJMenuBar(barra);

        setVisible(true);
    }

    private void crearBotones() {
        String[] botonesBasicos = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "=", "+",
            "C"
        };

        String[] botonesCientificos = {
            "sin", "cos", "tan", "√",
            "log", "ln", "^", "π"
        };

        for (String texto : botonesBasicos) {
            agregarBoton(texto);
        }

        if (modoCientifico) {
            for (String texto : botonesCientificos) {
                agregarBoton(texto);
            }
        }
    }

    private void agregarBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Arial", Font.BOLD, 18));
        boton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (texto) {
                    case "=":
                        calcular();
                        break;
                    case "C":
                        display.setText("");
                        break;
                    case "π":
                        display.setText(display.getText() + Math.PI);
                        break;
                    case "√":
                        try {
                            double valor = Double.parseDouble(display.getText());
                            display.setText(String.valueOf(Math.sqrt(valor)));
                        } catch (Exception ex) {
                            display.setText("Error");
                        }
                        break;
                    case "sin":
                    case "cos":
                    case "tan":
                        try {
                            double valorTrig = Math.toRadians(Double.parseDouble(display.getText()));
                            double resultado;
                            switch (texto) {
                                case "sin":
                                    resultado = Math.sin(valorTrig);
                                    break;
                                case "cos":
                                    resultado = Math.cos(valorTrig);
                                    break;
                                default:
                                    resultado = Math.tan(valorTrig);
                                    break;
                            }
                            display.setText(String.valueOf(resultado));
                        } catch (Exception ex) {
                            display.setText("Error");
                        }
                        break;
                    case "log":
                        try {
                            double valor = Double.parseDouble(display.getText());
                            display.setText(String.valueOf(Math.log10(valor)));
                        } catch (Exception ex) {
                            display.setText("Error");
                        }
                        break;
                    case "ln":
                        try {
                            double valor = Double.parseDouble(display.getText());
                            display.setText(String.valueOf(Math.log(valor)));
                        } catch (Exception ex) {
                            display.setText("Error");
                        }
                        break;
                    case "^":
                        display.setText(display.getText() + "^");
                        break;
                    default:
                        display.setText(display.getText() + texto);
                }
            }
        });
        panelBotones.add(boton);
    }

    private void calcular() {
        try {
            String expr = display.getText();
            if (expr.contains("^")) {
                String[] partes = expr.split("\\^");
                double base = Double.parseDouble(partes[0]);
                double exponente = Double.parseDouble(partes[1]);
                display.setText(String.valueOf(Math.pow(base, exponente)));
                return;
            }
            display.setText(String.valueOf(eval(expr)));
        } catch (Exception e) {
            display.setText("Error");
        }
    }

    // Evaluador básico para operaciones + - * /
    private double eval(String expr) {
        return new Object() {
            int pos = -1, ch;

            void siguienteChar() {
                ch = (++pos < expr.length()) ? expr.charAt(pos) : -1;
            }

            boolean comer(int charEsperado) {
                while (ch == ' ') siguienteChar();
                if (ch == charEsperado) {
                    siguienteChar();
                    return true;
                }
                return false;
            }

            double parse() {
                siguienteChar();
                double x = parseExpresion();
                if (pos < expr.length()) throw new RuntimeException("Caracter inesperado: " + (char) ch);
                return x;
            }

            double parseExpresion() {
                double x = parseTermino();
                for (;;) {
                    if (comer('+')) x += parseTermino();
                    else if (comer('-')) x -= parseTermino();
                    else return x;
                }
            }

            double parseTermino() {
                double x = parseFactor();
                for (;;) {
                    if (comer('*')) x *= parseFactor();
                    else if (comer('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (comer('+')) return parseFactor();
                if (comer('-')) return -parseFactor();

                double x;
                int inicio = this.pos;
                if (comer('(')) {
                    x = parseExpresion();
                    comer(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') siguienteChar();
                    x = Double.parseDouble(expr.substring(inicio, this.pos));
                } else {
                    throw new RuntimeException("Carácter inesperado: " + (char) ch);
                }

                return x;
            }
        }.parse();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CalculadoraGUI::new);
    }
}
