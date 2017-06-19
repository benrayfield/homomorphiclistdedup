/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package homomorphiclistdedupprototype;

import java.math.BigInteger;
import java.util.Random;

/** I was worried that since theres practically unlimited pseudorandom integers you could sum,
that you could take a small group of them and find the smallest sum of any pair within that group,
and do that for groups of groups recursively, and get 1/sqrt times closer
cuz (heads-tails)/sqrt(heads+tails) is a unit bellcurve
when all possible coin flips happen equally often,
which would allow doubling the number of brute force tries
to get you superexponentially closer,
but fortunately it turns out to be only as effective as random brute force search,
as you can see in the sum*cycles being near constant,
even while it continues to find smaller sums (of positives and negatives),
its not finding them much faster (or may even be slower?) than random guessing.
<br><br>
The homomorphic list dedup survives this attack,
but would not survive a quantum computer attack since they can efficiently do discretelog,
that is if quantum computers can scale without being subject to the same jitteryness
that makes subsetSum hard.
<br><br>
//sum*cycles, so the numbers are getting closer to 0 but only about as fast as a random search.
-9767942590775457564904264375886417642125811507648625611014655357780696156200 //recurse 1 branch 20
+150494684866853231802969314041633811753868149744500388031758608124120122000  //2 20
+3825090922682133458764215592547410552526001674539351631308448274060218208000 //3 20
-82795139614849262401824837626152108481262964251069857243060687911531520000   //4 20
+454473676250215216867059909937623171145135765359886889567680666377859200000  //5 20
-102970736524309454384398294617047756441961293361907332433265003607424000000  //6 20
*/
public class AverageCaseOfSubsetSumOfUnlimitedRandomIntegersSolvedRecursivelyButImpracticallyExpensive{
	
	static final BigInteger infinity = BigInteger.ONE.shiftLeft(257);
	
	public static BigInteger findSumNear0(int displayIfRecurseAtLeast, int recurse, int branchingFactor, Random rand){
		if(recurse == 0) return randSignedInt256(rand);
		BigInteger[] sums = new BigInteger[branchingFactor];
		for(int i=0; i<branchingFactor; i++){
			sums[i] = findSumNear0(displayIfRecurseAtLeast, recurse-1, branchingFactor, rand);
		}
		BigInteger nonnegativeSum = infinity; //find smallest magnitude sum of any 2
		BigInteger nonpositiveSum = infinity;
		for(int i=1; i<branchingFactor; i++){
			for(int j=0; j<i; j++){
				BigInteger b = sums[i].subtract(sums[j]);
				if(b.signum() >= 0){
					if(b.abs().compareTo(nonnegativeSum.abs()) < 0){
						nonnegativeSum = b;
					}
				}
				if(b.signum() <= 0){
					if(b.abs().compareTo(nonpositiveSum.abs()) < 0){
						nonpositiveSum = b;
					}
				}
			}
		}
		BigInteger sum;
		if(nonnegativeSum.equals(infinity)){
			if(nonpositiveSum.equals(infinity)){
				throw new RuntimeException("cant both be that big");
			}else{
				sum = nonpositiveSum;
			}
		}else{
			if(nonpositiveSum.equals(infinity)){
				sum = nonnegativeSum;
			}else{
				sum = rand.nextBoolean() ? nonnegativeSum : nonpositiveSum;
			}
		}
		if(recurse >= displayIfRecurseAtLeast){
			System.out.println("---");
			System.out.println("findSumNear0 recurse="+recurse+" returning "+sum);
			long cyclesApprox = (long)Math.pow(branchingFactor, recurse);
			System.out.println("cycles approx = "+cyclesApprox);
			System.out.println("sum*cycles = "+sum.multiply(BigInteger.valueOf(cyclesApprox)));
		}
		return sum;
	}
	
	public static BigInteger randSignedInt256(Random rand){
		byte[] b = new byte[32];
		rand.nextBytes(b);
		return new BigInteger(b);
	}
	
	
	public static void main(String[] args){
		int recurse = 5;
		int displayIfRecurseAtLeast = recurse-2;
		findSumNear0(displayIfRecurseAtLeast,recurse,20,Node.strongRand);
		System.out.println("Random integer "+randSignedInt256(Node.strongRand));
	}

}
