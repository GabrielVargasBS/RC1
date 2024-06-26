import java.io.*;
import java.net.*;

class Servidor {
    private static int portaServidorTCP = 6789;
    private static int portaServidorUDP = 9876;

    public static void main(String argv[]) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramSocket serverSocket = new DatagramSocket(portaServidorUDP);

                    while (true) {
                        byte[] receiveData = new byte[1024];
                        byte[] sendData = new byte[1024];

                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                        System.out.println("Aguardando datagrama do cliente....");
                        serverSocket.receive(receivePacket);

                        String fileName = new String(receivePacket.getData()).trim();
                        File file = new File("Arquivos/" + fileName);

                        if (file.exists()) {
                            sendData = "Sim".getBytes();
                        } else {
                            sendData = "Não".getBytes();
                        }

                        InetAddress ipCliente = receivePacket.getAddress();
                        int portaCliente = receivePacket.getPort();

                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipCliente, portaCliente);
                        serverSocket.send(sendPacket);
                        System.out.println("Enviado...");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(portaServidorTCP);
            
                    while (true) {
                        Socket conexao = serverSocket.accept();
            
                        DataInputStream entrada = new DataInputStream(conexao.getInputStream());
            
                        // Lê o nome do arquivo
                        String fileName = entrada.readUTF();
                        System.out.println("Nome do arquivo recebido: " + fileName);
            
                        // Lê o tamanho do arquivo
                        long fileSize = entrada.readLong();
                        System.out.println("Tamanho do arquivo recebido: " + fileSize);
            
                        // Cria um array de bytes do tamanho do arquivo
                        byte[] mybytearray = new byte[(int) fileSize];
            
                        // Lê os dados do arquivo
                        entrada.readFully(mybytearray);
                        System.out.println("Dados do arquivo recebidos.");
            
                        // Escreve os dados do arquivo para o disco
                        FileOutputStream fos = new FileOutputStream("Arquivos/" + fileName);
                        fos.write(mybytearray);
                        fos.flush();
                        fos.close();
                        System.out.println("Arquivo salvo em disco.");
            
                        DataOutputStream saida = new DataOutputStream(conexao.getOutputStream());
                        saida.writeUTF("Arquivo salvo com sucesso");
            
                        conexao.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }            
        }).start();
    }
}