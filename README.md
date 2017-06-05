# homomorphiclistdedup
Same secureHash by list content regardless of order of tree rotations and internal structure

Collision resistance is backed by subsetSum (which is npcomplete) wrapped around a prime,
or the secureHash used to generate arbitrary mySum of leafs (recommend SHA256),
or the secureHash used to wrap a forkEditable list in a leaf (recommend SHA256),
whichever is weaker. Recommend 192 bit integers if your system cant stand even 1 collision,
but remember this is experimental.

hash(listX) = pair(mySum(listX)%globalModulus,myMult(listX)%globalModulus)

mySum(listX) = isSparseRange(y) ? 0 : sum<y in listX>(mySum(y)*globalLeafMult^index(y,x))

myMult(listX) = isLeaf(listX) ? 1 : multiply<y in listX>(myMult(y))

TODO verify this math matches the code (which passes testcases).

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
	
	Node xyxyxyx1 = x.concat(y).concat(x).concat(y).concat(x).concatEmptyIndexs(5000).concat(x).concat(y);
	Node xyxyxyx2 = x.concat(y).concat(x).concat(y.concat(x).concatEmptyIndexs(2000).concatEmptyIndexs(3000).concat(x).concat(y));
	//Node xyxyxyx2 = x.concat(y).concat(x).concat(y.concat(x).concatEmptyIndexs(2000).concatEmptyIndexs(3000).concat(x).concat(y));
	System.out.println("xyxyxyx1=?xyxyxyx2 "+xyxyxyx1.equals(xyxyxyx2)+" (should be true)");
	System.out.println("xyxyxyx1=?x "+xyxyxyx1.equals(x)+" (should be false)");