package com.dabomstew.pkrandom;
/*
 * Randomize the type matchup chart
 * Special thanks to coolboyman for his post on the format of the type chart:
 * https://www.pokecommunity.com/showthread.php?t=83674
 *
 * Known offsets
 * Red and Blue: 3E474
 * Yellow: 3E62D
 * Gold and Silver: 34D01
 * Crystal: 34BB1
 * Ruby: 1F9720
 * Sapphire: 1F96B0
 * Emerald: 31ACE8
 * Fire Red: 24F050
 * Leaf Green: 24F02C
 * Diamond: 1DE1B8
 * Platinum  23AD94 - 23AEE1
 *
 * There seems to be little space for expanding the number of matchups
 * In order to both put a limit on the amount of matchups generated and
 * limit the complexity, the current implementation uses a markov approach
 */
import java.util.Random;
import java.util.Arrays;
import java.util.Scanner;
import java.io.RandomAccessFile;

public class RandomTypeMatchups {
   public byte basetypes[] = {0x00, 0x05, 0x05, 0x00, 0x08, 0x05, 0x0a, 0x0a, 0x05,
       0x0a, 0x0b, 0x05, 0x0a, 0x0c, 0x14, 0x0a, 0x0f, 0x14, 0x0a, 0x06, 0x14,
       0x0a, 0x05, 0x05, 0x0a, 0x10, 0x05, 0x0a, 0x08, 0x14, 0x0b, 0x0a, 0x14,
       0x0b, 0x0b, 0x05, 0x0b, 0x0c, 0x05, 0x0b, 0x04, 0x14, 0x0b, 0x05, 0x14,
       0x0b, 0x10, 0x05, 0x0d, 0x0b, 0x14, 0x0d, 0x0d, 0x05, 0x0d, 0x0c, 0x05,
       0x0d, 0x04, 0x00, 0x0d, 0x02, 0x14, 0x0d, 0x10, 0x05, 0x0c, 0x0a, 0x05,
       0x0c, 0x0b, 0x14, 0x0c, 0x0c, 0x05, 0x0c, 0x03, 0x05, 0x0c, 0x04, 0x14,
       0x0c, 0x02, 0x05, 0x0c, 0x06, 0x05, 0x0c, 0x05, 0x14, 0x0c, 0x10, 0x05,
       0x0c, 0x08, 0x05, 0x0f, 0x0b, 0x05, 0x0f, 0x0c, 0x14, 0x0f, 0x0f, 0x05,
       0x0f, 0x04, 0x14, 0x0f, 0x02, 0x14, 0x0f, 0x10, 0x14, 0x0f, 0x08, 0x05,
       0x0f, 0x0a, 0x05, 0x01, 0x00, 0x14, 0x01, 0x0f, 0x14, 0x01, 0x03, 0x05,
       0x01, 0x02, 0x05, 0x01, 0x0e, 0x05, 0x01, 0x06, 0x05, 0x01, 0x05, 0x14,
       0x01, 0x11, 0x14, 0x01, 0x08, 0x14, 0x03, 0x0c, 0x14, 0x03, 0x03, 0x05,
       0x03, 0x04, 0x05, 0x03, 0x05, 0x05, 0x03, 0x07, 0x05, 0x03, 0x08, 0x00,
       0x04, 0x0a, 0x14, 0x04, 0x0d, 0x14, 0x04, 0x0c, 0x05, 0x04, 0x03, 0x14,
       0x04, 0x02, 0x00, 0x04, 0x06, 0x05, 0x04, 0x05, 0x14, 0x04, 0x08, 0x14,
       0x02, 0x0d, 0x05, 0x02, 0x0c, 0x14, 0x02, 0x01, 0x14, 0x02, 0x06, 0x14,
       0x02, 0x05, 0x05, 0x02, 0x08, 0x05, 0x0e, 0x01, 0x14, 0x0e, 0x03, 0x14,
       0x0e, 0x0e, 0x05, 0x0e, 0x11, 0x00, 0x0e, 0x08, 0x05, 0x06, 0x0a, 0x05,
       0x06, 0x0c, 0x14, 0x06, 0x01, 0x05, 0x06, 0x03, 0x05, 0x06, 0x02, 0x05,
       0x06, 0x0e, 0x14, 0x06, 0x07, 0x05, 0x06, 0x11, 0x14, 0x06, 0x08, 0x05,
       0x05, 0x0a, 0x14, 0x05, 0x0f, 0x14, 0x05, 0x01, 0x05, 0x05, 0x04, 0x05,
       0x05, 0x02, 0x14, 0x05, 0x06, 0x14, 0x05, 0x08, 0x05, 0x07, 0x00, 0x00,
       0x07, 0x0e, 0x14, 0x07, 0x11, 0x05, 0x07, 0x08, 0x05, 0x07, 0x07, 0x14,
       0x10, 0x10, 0x14, 0x10, 0x08, 0x05, 0x11, 0x01, 0x05, 0x11, 0x0e, 0x14,
       0x11, 0x07, 0x14, 0x11, 0x11, 0x05, 0x11, 0x08, 0x05, 0x08, 0x0a, 0x05,
       0x08, 0x0b, 0x05, 0x08, 0x0d, 0x05, 0x08, 0x0f, 0x14, 0x08, 0x05, 0x14,
       0x08, 0x08, 0x05,       0x00, 0x07, 0x00, 0x01, 0x07, 0x00};
   private String types[] = {"Normal", "Fighting", "Flying", "Poison", "Ground",
      "Rock", "Bug", "Ghost", "Steel", "Fire", "Water", "Grass", "Electric",
      "Psychic", "Ice", "Dragon", "Dark"};
   private String types_abr[] = {"nor", "fig", "fly", "poi", "gro", "roc",
      "bug", "gho", "ste", "fir", "wat", "gra", "ele", "psy", "ice", "dra", "dar"};
   private byte type_table[] = {0,1,2,3,4,5,6,7,8,-1,9,10,11,12,13,14,15,16};
   private byte type_LUT[] = {0,1,2,3,4,5,6,7,8,10,11,12,13,14,15,16,17};
   private byte effec_table[] = {0, 5, 20};
   private double deviation = .2;
   Random r;
   public RandomTypeMatchups(Random r) {
      this.r = r;
   }
   private RandomTypeMatchups() {
      this.r = new java.util.Random();
   }

   public byte[] randomTypes() {
      //compute base distribution
      int[][][] count = {{new int[17], new int[17], new int[17]},
                        {new int[17], new int[17], new int[17]}};
      int[] effec_sum = new int[3];
      for(int i=0; i<basetypes.length; i+=3) {
         int mult = basetypes[i+2];
         int mult_index = 0;
         if (mult >= 20)
            mult_index = 2;
         else if (mult >= 5)
            mult_index = 1;

         effec_sum[mult_index]++;
         count[0][mult_index][type_table[basetypes[i]]]+=1;
         count[1][mult_index][type_table[basetypes[i+1]]]+=1;
      }
      //actually generate new matchups
      byte[] matchups = new byte[basetypes.length+3];
outerloop:
      for(int i=0;i<basetypes.length;i+=3) {
         int effi = pickWeighted(effec_sum, deviation/5);
         int ai = pickWeighted(count[0][effi], deviation);
         byte at = type_LUT[ai];
         int di = pickWeighted(count[1][effi], deviation);
         byte dt = type_LUT[di];
         byte eff = effec_table[effi];

         //Check and retry duplicates
         //Currently makes randomTypes() O(n^2), consider improved implementation.
         for(int x=0;x<i;x+=3) {
            if (matchups[x] == at && matchups[x+1] == dt) {
               //count[0][effi][ai]++;
               //count[1][effi][di]++;
               //effec_sum[effi]++;
               i-=3;
               continue outerloop;
            }
         }
         matchups[i] = at;
         matchups[i+1] = dt;
         matchups[i+2] = eff;
      }
      //Adjust ghost for foresight;
      int end = basetypes.length;
      for(int i=0;i<end;i++) {
         if (matchups[i+2] == 0 && matchups[i+1] == 7) {
            System.arraycopy(matchups, i, matchups, end, 3);
            end -= 3;
            System.arraycopy(matchups, end, matchups, i, 3);
            i -= 3;
         }
      }
      if(end == basetypes.length)
         System.err.println("Warning, no ghost immunities generated. Foresight may break");
      matchups[end] = (byte)-2;
      matchups[end+1] = (byte)-2;
      matchups[end+2] = 0x00;
      return matchups;
   }
   public int pickWeighted(int[] sums, double deviation) {
      int sum_of_sums = 0;
      for(int i = 0; i < sums.length; i++) {
         sum_of_sums+=sums[i];
      }
      if(sum_of_sums < 0) {
         //occurs when a type effectiveness is picked too many times
         //In this case, weighting no longer matters
         sum_of_sums = 0;
      }
      double step = sum_of_sums*deviation/sums.length;
      double p = r.nextDouble()*sum_of_sums*(1+deviation);
      for(int i=0;i<sums.length;i++) {
         if(p <= sums[i]+step) {
            //sums[i]--;
            return i;
         }
         //TODO: double check correctness when sums[i]+deviation < 0
         //if(sums[i]+step<0)
           // System.err.println("Warning, sum+step is negative");
         p -= (sums[i]+step);
      }
      System.err.printf("Failed to find element with p of %f remaining\n",p);
      return sums.length-1;
   }
   private char effec_char[] = {' ','0','-','+'};
   public String formatChart(byte[] matchups) {
      String out="    ";
      int dim = 17;
      int table[] = new int[dim*dim];
      for(int i=0; i<matchups.length; i+=3) {
         if(matchups[i] == -2 && matchups[i+1] == -2)
            continue;
         int mult = matchups[i+2];
         int mult_index = 0;
         if (mult >= 20)
            mult_index = 2;
         else if (mult >= 5)
            mult_index = 1;
         table[type_table[matchups[i]]*dim+type_table[matchups[i+1]]]=mult_index+1;
      }
      for(int i=0;i<17;i++)
         out+=types_abr[i]+' ';
      for(int r=0; r<17;r++) {
         out+='\n'+types_abr[r];
         for(int c=0; c<17;c++) {
            out+="   "+effec_char[table[r*17+c]];
         }
      }
      return out;
   }
   private static byte[] platinum_sig = {0x50, 0x4f, 0x4b, 0x45, 0x4d, 0x4f,
           0x4e, 0x20, 0x50, 0x4c, 0x00, 0x00, 0x43, 0x50, 0x55, 0x45};
   public static void main(String[] args) throws Exception {
      Scanner s = new Scanner(System.in);
      String file_name = "";
      if(args.length>=1)
         file_name = args[0];
      else {
         System.out.print("Enter the Filename: ");
         file_name = s.nextLine();
      }
      RandomAccessFile f = new RandomAccessFile(file_name, "rw");
      byte[] sig = new byte[platinum_sig.length];
      f.readFully(sig);
      for(int i=0;i<platinum_sig.length;i++) {
         if(platinum_sig[i] != sig[i]) {
            System.err.println("Invalid signiture");
         }
      }
      RandomTypeMatchups rtm = new RandomTypeMatchups(new Random());
      byte[] new_matchups = rtm.randomTypes();
      System.out.println(rtm.formatChart(new_matchups));
      byte[] matchups_byte = new byte[new_matchups.length];
      for(int i=0;i<new_matchups.length;i++) {
         matchups_byte[i] = (byte)new_matchups[i];
      }
      f.seek(0x23AD94);
      f.write(matchups_byte);
      f.close();
      System.out.println("Press any key to close");
      s.next();
   }
}


