
import java.io.*;                                                      //(A)

class appendchar {                                                       //(B)

     public static void main( String[] args )                          //(C)
     {
          int ch = 0;
          FileInputStream in = null;                                   //(D)
          FileOutputStream out = null;                                 //(E)
          if ( args.length != 2 ) {                                    //(F)
               System.err.println( "usage: java FileCopy source dest" );
               System.exit( 0 );
          }
          try {
               in = new FileInputStream( args[0] );                    //(G)
               out = new FileOutputStream( args[1] );                  //(H)

               while ( true ) {
                    ch = in.read();                                    //(I)
                    if (ch == -1) break;
                    out.write(ch);                                     //(J)
               }
               out.close();                                            //(K)
               in.close();                                             //(L)
          } catch (IOException e) {
               System.out.println( "IO error" );
          }
     }
}
