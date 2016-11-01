import java.util.ArrayList;

import java.util.Random;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
public class BloomFilter {

	//

	public static final int P = 4000037;
	public static final int M = 3276800;
	public static final int numRandomWords = 100;
	public static final int numHashFunctions = 7;
	public static int[] filter = new int[M/32]; //400kb filter

	public static int[][] hashFunctions = new int[numHashFunctions][2]; //represents a and b values
	public static void main(String[] args) throws IOException {
		//Generate our 7 hash functions
		double avgFalsePos = 0;
		for(int z =0; z < 100; z++){
			for(int x = 0; x < filter.length; x ++)
				filter[x] = 0;
			ArrayList<Integer> hashVals = new ArrayList<Integer>();
			ArrayList<String> dictionary = new ArrayList<String>();
			ArrayList<String> randWords = new ArrayList<String>();
			Random rand = new Random();
			for(int i = 0; i< numHashFunctions; i++){
				hashFunctions[i][0] = rand.nextInt(P);
				if(!hashVals.contains(hashFunctions[i][0]))
					hashVals.add(hashFunctions[i][0]);
				else{
					i--;
				}
			}
			hashVals.clear();
			for(int i = 0; i< numHashFunctions; i++){
				hashFunctions[i][1] = rand.nextInt(P)+1;
				if(!hashVals.contains(hashFunctions[i][1]))
					hashVals.add(hashFunctions[i][1]);
				else{
					i--;
				}
			}

			//Put words from xlsx into the filter
			FileReader fr = new FileReader("words.txt");
			BufferedReader br = new BufferedReader(fr);
			String str = "";

			while ((str = br.readLine()) != null) {

				hash(str);
				dictionary.add(str);

			}       
			int countFalsePositive = 0;
			//Generate 100 random words.
			final String AB = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

			for(int i = 0; i< numRandomWords; i++){
				String s = "";
				for(int j = 0; j < 5; j++){
					s += AB.charAt(rand.nextInt(AB.length()));

				}
				if(randWords.contains(s)){
					i--;
				}
				else{

					randWords.add(s);
				}
			}

			for(String w : randWords){

				if(checkHash(w) && !dictionary.contains(w)){
					countFalsePositive++;
				}

			}
			System.out.println(countFalsePositive);
			avgFalsePos += countFalsePositive;
		}
		System.out.println(avgFalsePos/100);
	}

	public static void hash(String s){
		int code = s.hashCode();
		if(code < 0) code *= -1;
		for(int i = 0; i<numHashFunctions; i++){
			BigInteger x = BigInteger.valueOf(hashFunctions[i][1]);
			BigInteger y = BigInteger.valueOf(code);
			x = x.multiply(y).add(BigInteger.valueOf(hashFunctions[i][0])).mod(BigInteger.valueOf(P));
			int mult = x.intValue();
			int index = mult % M;

			filter[index/32] |= (1 << (index%32));
		}
	}

	public static boolean checkHash(String s){
		int code = s.hashCode();
		if(code < 0) code *= -1;
		for(int i = 0; i<numHashFunctions; i++){
			BigInteger x = BigInteger.valueOf(hashFunctions[i][1]);
			BigInteger y = BigInteger.valueOf(code);
			x = x.multiply(y).add(BigInteger.valueOf(hashFunctions[i][0])).mod(BigInteger.valueOf(P));
			int mult = x.intValue();
			int index = mult % M;
			int bitCheck = filter[index/32] & (1 << (index%32));


			if(bitCheck == 0){

				return false;
			}

		}
		return true;
	}
}
