import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

class Cliente {
    private static String ipServidor = "127.0.0.1";
    private static int portaServidorTCP = 6789;
    private static int portaServidorUDP = 9876;

    public static void main(String argv[]) throws Exception {
        JFrame frame = new JFrame("Trabalho TCP/UDP");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JButton sendFileButton = new JButton("Envie um arquivo");
        sendFileButton.setBounds(50, 20, 200, 25);
        sendFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sendFile();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
        panel.add(sendFileButton);

        JButton checkFileButton = new JButton("Verifique um arquivo");
        checkFileButton.setBounds(50, 60, 200, 25);
        checkFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    checkFile();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
        panel.add(checkFileButton);
    }

    private static void sendFile() throws Exception {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            System.out.println("Conectando ao servidor...");

            Socket socket = new Socket(ipServidor, portaServidorTCP);

            System.out.println("Conexão estabelecida.");

            DataOutputStream saida = new DataOutputStream(socket.getOutputStream());
            saida.writeUTF(selectedFile.getName());

            // Envia o tamanho do arquivo
            saida.writeLong(selectedFile.length());

            System.out.println("Enviando arquivo...");

            byte[] mybytearray = new byte[(int) selectedFile.length()];
            FileInputStream fis = new FileInputStream(selectedFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(mybytearray, 0, mybytearray.length);
            OutputStream os = socket.getOutputStream();
            os.write(mybytearray, 0, mybytearray.length);
            os.flush();

            System.out.println("Arquivo enviado.");

            DataInputStream entrada = new DataInputStream(socket.getInputStream());
            String confirmacao = entrada.readUTF();
            
            JOptionPane.showMessageDialog(null, confirmacao);
            
            System.out.println("Conexão encerrada.");
            socket.close();
        }
    }

    private static void checkFile() throws Exception {
        String fileName = JOptionPane.showInputDialog("Digite o nome do arquivo para verificar:");

        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName(ipServidor);
        byte[] sendData = fileName.getBytes();

        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, portaServidorUDP);
        clientSocket.send(sendPacket);

        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        clientSocket.close();

        String resposta = new String(receivePacket.getData()).trim();
        JOptionPane.showMessageDialog(null, "O servidor " + (resposta.equals("Sim") ? "possui" : "não possui") + " o arquivo " + fileName);
    }
}