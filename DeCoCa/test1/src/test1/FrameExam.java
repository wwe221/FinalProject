package test1;


import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
 
 
public class FrameExam extends JFrame
{
    JLabel lbl;
    JLabel la1,la2,la3,la4,la5,la6,la7,la8,la9,la10,la11,la12;
    JTextField tx1,tx2,tx3,tx4,tx5,tx6,tx7,tx8,tx9,tx10,tx11,tx12;
    
    JPanel statuspanel,panel;
    
    JTextArea content;
 
    public FrameExam()
    {
          super( "���� status" );	
          // FlowLayout���
          setLayout( new FlowLayout() );
          // Border�� ���� ����
          EtchedBorder eborder =  new EtchedBorder();
          // ���̺� ����     
          lbl = new JLabel( "                ���� �����Դϴ�.              " );
          // ���̺� ���� �����
          lbl.setBorder(eborder);
          // �г��߰�
          statuspanel = new JPanel();
          // ���̺� �߰�
          statuspanel.add(lbl);
          add( statuspanel );
          ////////////////////////////////////////////////
          // id�гΰ� pw �гλ���
          panel = new JPanel();
          
          la1 = new JLabel("���͸�");
          la2 = new JLabel("�ڵ�����");
          la3 = new JLabel("�ӵ�");
          la4 = new JLabel("Ÿ�̾� ����� �տ�");
          la5 = new JLabel("Ÿ�̾� ����� �տ�");
          la6 = new JLabel("Ÿ�̾� ����� �޿�");
          la7 = new JLabel("Ÿ�̾� ����� �޿�");
          la8 = new JLabel("���οµ�");
          la9 = new JLabel("������");
          la10 = new JLabel("������Ʈ");
          la11 = new JLabel("�극��ũ");
          la12 = new JLabel("����");
          
          // id�ؽ�Ʈ�ʵ�� pw�ؽ�Ʈ �ʵ� ����
          tx1 = new JTextField(20);
          tx2 = new JTextField(20);
          tx3 = new JTextField(20);
          tx4 = new JTextField(20);
          tx5 = new JTextField(20);
          tx6 = new JTextField(20);
          tx7 = new JTextField(20);
          tx8 = new JTextField(20);
          tx9 = new JTextField(20);
          tx10 = new JTextField(20);
          tx11 = new JTextField(20);
          tx12 = new JTextField(20);
          
          add(la1); add(tx1);
          add( la2 ); add( tx2 );
          add( la3 ); add( tx3 );
          add( la4 ); add( tx4 );
          add( la5 ); add( tx5 );
          add( la6); add( tx6 );
          add( la7); add( tx7 );
          add( la8 ); add( tx8 );
          add( la9 ); add( tx9 );
          add( la10 ); add( tx10 );
          add( la11 ); add( tx11 );
          add( la12 ); add( tx12 );
          
          

         
          setSize( 250, 700 );
          setVisible(true);
          setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    
    public static void main( String args[] )
       { 
        new FrameExam();
       }
 
    
}