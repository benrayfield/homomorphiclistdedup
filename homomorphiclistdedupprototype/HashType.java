package homomorphiclistdedupprototype;
import java.math.BigInteger;

public class HashType{
	
	public final BigInteger globalLeafSize, globalModulus;
	
	public HashType(BigInteger globalLeafSize, BigInteger globalModulus){
		this.globalLeafSize = globalLeafSize;
		this.globalModulus = globalModulus;
	}
	
	public boolean equals(Object o){
		if(!(o instanceof HashType)) return false;
		HashType h = (HashType) o;
		return globalLeafSize.equals(h.globalLeafSize) && globalModulus.equals(h.globalModulus);
	}

}
