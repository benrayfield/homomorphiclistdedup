/** Ben F Rayfield offers this software opensource GNU GPL 2+ */
package homomorphiclistdedupprototype;
import java.math.BigInteger;
import java.security.SecureRandom;

/** Can be leaf (if low and high nodes are null) or nonleaf.
Nonleafs are a sparse branch of 2 nodes optionally with empty indexs between.
The amount skipped (between low and high) is size-low.size-high.size.
This is a model of how the list part of maplist will work in humanAiNet.
humanAiNet will use more optimized datastructs. This is an early prototype.
*/
public class Node{
	
	/** one of two parts of hash */
	public final BigInteger primeWrappedSum;
	
	/** one of two parts of hash */
	public final BigInteger primeWrappedExponential;
	
	public final HashType hashType;
	
	public final long size;
	
	/** 2 childs, both null if leaf, else both nonnull */
	public final Node low, high;
	
	/** leaf */
	public Node(BigInteger primeWrappedSum, BigInteger primeWrappedExponential, HashType hashType){
		this(primeWrappedSum, primeWrappedExponential, hashType, 1, null, null);
	}
	
	public Node(BigInteger primeWrappedSum, BigInteger primeWrappedExponential, HashType hashType, long size, Node low, Node high){
		if(size >= 1L<<62) throw new RuntimeException("Too big: "+size);
		this.primeWrappedSum = primeWrappedSum;
		this.primeWrappedExponential = primeWrappedExponential;
		this.hashType = hashType;
		this.size = size;
		this.low = low;
		this.high = high;
	}
	
	/** TODO is this off by up to plus/minus 2? */
	public long skippedIndexs(){
		return low==null? 0 : size-low.size-high.size;
	}
	
	public Node concat(Node u){
		if(!hashType.equals(u.hashType)) throw new RuntimeException("Different hashType");
		return new Node(
			primeWrappedSum.add(primeWrappedExponential.multiply(u.primeWrappedSum)).mod(hashType.globalModulus), //primeWrappedSum
			primeWrappedExponential.multiply(u.primeWrappedExponential).mod(hashType.globalModulus), //primeWrappedExponential
			hashType,
			size+u.size,
			this,
			u
		);
	}
	
	/** Used for sparse skipping */
	public Node concatEmptyIndexs(long skip){
		if(size+skip >= 1L<<62) throw new RuntimeException("Too big size="+size+" skip="+skip);
		BigInteger mult = hashType.globalLeafSize.modPow(BigInteger.valueOf(skip), hashType.globalModulus);
		BigInteger newPrimeWrappedExponential = primeWrappedExponential.multiply(mult).mod(hashType.globalModulus);
		return new Node(primeWrappedSum, newPrimeWrappedExponential, hashType, size+skip, low, high); //FIXME high increased
	}
	
	public static final SecureRandom strongRand;
	static{
		strongRand = new SecureRandom();
		strongRand.setSeed(3+System.nanoTime()*49999+System.currentTimeMillis()*new Object().hashCode());
	}
	
	public String toString(){
		return "(uflist "+primeWrappedSum+" "+primeWrappedExponential+")";
	}
	
	public static BigInteger randPositivePrime(int bits){
		return new BigInteger(bits, 500, strongRand);
	}
	
	public static BigInteger randPrimePosOrNeg(int bits){
		BigInteger b = randPositivePrime(bits-1);
		return strongRand.nextBoolean() ? b : b.negate();
	}
	
	public boolean equals(Object o){
		if(!(o instanceof Node)) return false;
		Node u = (Node)o;
		return primeWrappedSum.equals(u.primeWrappedSum)
			&& primeWrappedExponential.equals(u.primeWrappedExponential)
			&& hashType.equals(u.hashType);
	}
	
	public int hashCode(){
		return primeWrappedSum.intValue()^primeWrappedExponential.intValue();
	}
	
	public static void main(String[] args){
		BigInteger mod = randPrimePosOrNeg(256);
		BigInteger leafMult = randPrimePosOrNeg(200); //BigInteger.valueOf(2); //must be less than mod
		HashType h = new HashType(leafMult, mod);
		Node x = new Node(randPrimePosOrNeg(256), leafMult, h);
		Node y = new Node(randPrimePosOrNeg(256), leafMult, h);
		Node m = new Node(randPrimePosOrNeg(256), leafMult, h);
		Node n = new Node(randPrimePosOrNeg(256), leafMult, h);
		for(int i=0; i<20; i++){
			System.out.println("x="+x);
			System.out.println("y="+y);
			Node axyb = x.concat(y);
			System.out.println("axyb="+axyb);
			Node aaxybxb = axyb.concat(x);
			System.out.println("aaxybxb="+aaxybxb);
			Node ayxb = y.concat(x);
			System.out.println("ayxb="+ayxb);
			Node axayxbb = x.concat(ayxb);
			Node axxb = x.concat(x);
			Node aaxxbxb = axxb.concat(x);
			System.out.println("? aaxxbxb="+aaxxbxb);
			System.out.println("? aaxybxb="+aaxybxb);
			System.out.println(aaxybxb.equals(axayxbb)+" (should be true)");
			System.out.println(axxb.equals(aaxxbxb)+" (should be false)");
			System.out.println(aaxybxb.equals(aaxxbxb)+" (should be false)");
			System.out.println("aaxybxb="+aaxybxb);
			System.out.println("axayxbb="+axayxbb);
			Node xyxyxyx1 = x.concat(y).concat(x).concat(y).concat(x).concatEmptyIndexs(5000).concat(x).concat(y);
			Node xyxyxyx2 = x.concat(y).concat(x).concat(y.concat(x).concatEmptyIndexs(2000).concatEmptyIndexs(3000).concat(x).concat(y));
			System.out.println("xyxyxyx1=?xyxyxyx2 "+xyxyxyx1.equals(xyxyxyx2)+" (should be true)");
			System.out.println("xyxyxyx1=?x "+xyxyxyx1.equals(x)+" (should be false)");
			System.out.println("-------"+i+"---------");
			
			x = aaxybxb.concat(m); //concat m and n since both are x y x
			y = axayxbb.concat(n);
		}
	}

}