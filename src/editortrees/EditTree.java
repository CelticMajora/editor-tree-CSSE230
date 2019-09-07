package editortrees;

import java.util.ArrayList;

import editortrees.Node.Code;

// A height-balanced binary tree with rank that could be the basis for a text editor.

public class EditTree {

	private Node root;
	public static final Node NULL_NODE = new Node(null, null, -1);
	private int currentSize;

	/**
	 * MILESTONE 1
	 * Construct an empty tree
	 */
	public EditTree() {
		this.root = NULL_NODE;
		this.currentSize = -1;
	}

	/**
	 * MILESTONE 1
	 * Construct a single-node tree whose element is cc
	 * 
	 * @param ch
	 */
	public EditTree(char ch) {
		this.root = new Node(ch, this);
		this.currentSize = 0;
	}

	/**
	 * MILESTONE 2
	 * Make this tree be a copy of e, with all new nodes, but the same shape and
	 * contents.
	 * 
	 * @param e
	 */
	public EditTree(EditTree e) {
		this.currentSize = e.size();
		StringHolder chars = new StringHolder("");
		StringHolder children = new StringHolder("");
		ArrayList<Integer> ranks = new ArrayList<Integer>();
		StringHolder balanceCodes = new StringHolder("");
		e.root.fillCoder(chars, children, ranks, balanceCodes);
		if(chars.getData().length() == 0){
			this.root = NULL_NODE;
		}
		else{
			Code rootBalance = null;
			switch(balanceCodes.getData().charAt(0)){
				case '\\':
					rootBalance = Code.RIGHT;
					break;
				case '/':
					rootBalance = Code.LEFT;
					break;
				case '=':
					rootBalance = Code.SAME;
					break;
			}
			this.root = new Node(chars.getData().charAt(0), this, ranks.get(0), rootBalance , NULL_NODE);
			chars.setData(chars.getData().substring(1));
			ranks.remove(0);
			balanceCodes.setData(balanceCodes.getData().substring(1));
			if(chars.getData().length() != 0){
				this.root.fillTree(chars, children, ranks, balanceCodes);
				this.root.fillCoder(chars, children, ranks, balanceCodes);
			}
		}
	}
	
	public class StringHolder{
		String data;
		public StringHolder(String data){
			this.data = data;
		}
		public String getData(){
			return this.data;
		}
		public void setData(String data){
			this.data = data;
		}
	}

	/**
	 * MILESTONE 3
	 * Create an EditTree whose toString is s. This can be done in O(N) time,
	 * where N is the length of the tree (repeatedly calling insert() would be
	 * O(N log N), so you need to find a more efficient way to do this.
	 * 
	 * @param s
	 */
	public EditTree(String s) {
		//inefficient way
//		this.root = new Node(s.charAt(0), this);
//		for(int i = 1; i < s.length(); i++){
//			this.add(s.charAt(i));
//		}
		//TODO write an efficient way to do this.
		this.currentSize = s.length();
		if(this.currentSize == 0){
			this.root = NULL_NODE;
		}
		else if(this.currentSize == 1){
			this.root = new Node(s.charAt(0), this);
		}
		else if(this.currentSize == 2){
			this.root = new Node(s.charAt(0), this);
			this.root.balance = Code.RIGHT;
			this.root.right = new Node(s.charAt(1), this.root, this);
		}
		else{
			this.root = this.stringTreeMaker(NULL_NODE, s);
		}		
	}
	
	private Node stringTreeMaker(Node parent, String s) {
		// TODO Auto-generated method stub.
		int center = s.length() / 2;
		String start = s.substring(0, center);
		String end = s.substring(center + 1);
		Node toReturn = new Node(s.charAt(center), parent, this);
		toReturn.rank = start.length();
		int sLeng = start.length();
		int eLeng = end.length();
		if(s.length() % 2 == 0){
			toReturn.balance = Code.LEFT;
		}		
		if(sLeng == 2 && eLeng == 2){
			toReturn.left = new Node(start.charAt(1), toReturn, this);
			toReturn.left.left = new Node(start.charAt(0), toReturn.left, this);
			toReturn.right = new Node(end.charAt(0), toReturn, this);
			toReturn.right.right = new Node(end.charAt(1), toReturn, this);
		}
		else if(sLeng > 2 && eLeng == 2){
			toReturn.left = this.stringTreeMaker(toReturn, start);
			toReturn.right = new Node(end.charAt(0), toReturn, this);
			toReturn.right.right = new Node(end.charAt(1), toReturn.left, this);
		}		
		else if(sLeng == 1 || eLeng == 1){
			toReturn.left = new Node(start.charAt(0), toReturn, this);
			toReturn.right = new Node(end.charAt(0), toReturn, this);
			if (sLeng == 2) {
				toReturn.left = new Node(start.charAt(1), toReturn, this);
				toReturn.left.left = new Node(start.charAt(0), toReturn, this);
			}
			if (eLeng == 2) {
				toReturn.right = new Node(end.charAt(1), toReturn, this);
				toReturn.right.right = new Node(end.charAt(0), toReturn, this);
			}
		}
		else{
			toReturn.left = this.stringTreeMaker(toReturn, start);
			toReturn.right = this.stringTreeMaker(toReturn, end);
		}
		return toReturn;
	}

	/**
	 * MILESTONE 1
	 * returns the total number of rotations done in this tree since it was
	 * created. A double rotation counts as two.
	 *
	 * @return number of rotations since tree was created.
	 */
	public int totalRotationCount() {
		return this.root.numRotations;
	}

	/**
	 * MILESTONE 1
	 * return the string produced by an inorder traversal of this tree
	 */
	@Override
	public String toString() {
		String toReturn = "";
		for(Character c : this.toArrayList()){
			toReturn = toReturn + c;
		}
		return toReturn;
	}

	private ArrayList<Character> toArrayList() {
		ArrayList<Character> toReturn = new ArrayList<Character>();
		if(this.isEmpty()){
			return toReturn;
		}
		this.root.toArrayList(toReturn);
		return toReturn;
	}

	private boolean isEmpty() {
		return this.root == NULL_NODE;
	}

	/**
	 * MILESTONE 1
	 * This one asks for more info from each node. You can write it like 
	 * the arraylist-based toString() method from the
	 * BST assignment. However, the output isn't just the elements, but the
	 * elements, ranks, and balance codes. Former CSSE230 students recommended
	 * that this method, while making it harder to pass tests initially, saves
	 * them time later since it catches weird errors that occur when you don't
	 * update ranks and balance codes correctly.
	 * For the tree with node b and children a and c, it should return the string:
	 * [b1=, a0=, c0=]
	 * There are many more examples in the unit tests.
	 * 
	 * @return The string of elements, ranks, and balance codes, given in
	 *         a pre-order traversal of the tree.
	 */
	public String toDebugString() {
		ArrayList<String> toReturn = new ArrayList<String>();
		this.root.toDebugString(toReturn);
		return toReturn.toString();
	}

	/**
	 * MILESTONE 1
	 * @param ch
	 *            character to add to the end of this tree.
	 */
	public void add(char ch) {
		// Notes:
		// 1. Please document chunks of code as you go. Why are you doing what
		// you are doing? Comments written after the code is finalized tend to
		// be useless, since they just say WHAT the code does, line by line,
		// rather than WHY the code was written like that. Six months from now,
		// it's the reasoning behind doing what you did that will be valuable to
		// you!
		// 2. Unit tests are cumulative, and many things are based on add(), so
		// make sure that you get this one correct.
		if(this.isEmpty()){
			this.root = new Node(ch, this);
			this.currentSize++;
			return;
		}
		this.root.add(ch);
		this.currentSize ++;
	}

	/**
	 * MILESTONE 1
	 * @param ch
	 *            character to add
	 * @param pos
	 *            character added in this inorder position
	 * @throws IndexOutOfBoundsException
	 *             id pos is negative or too large for this tree
	 */
	public void add(char ch, int pos) throws IndexOutOfBoundsException {
		if(this.root == NULL_NODE && pos == 0){
			this.root = new Node(ch, this);
			this.currentSize ++;
			return;
		}
//		if(pos > this.currentSize + 1 || pos < 0){
//			throw new IndexOutOfBoundsException();
//		}
		if(pos > this.size() || pos < 0){
			throw new IndexOutOfBoundsException("Pos = " + pos + ", and size = " + this.size());
		}
		this.root.add(ch, pos);
		this.currentSize ++;
		
	}

	/**
	 * MILESTONE 1
	 * @param pos
	 *            position in the tree
	 * @return the character at that position
	 * @throws IndexOutOfBoundsException
	 */
	public char get(int pos) throws IndexOutOfBoundsException {
		if(pos > this.currentSize || pos < 0){
			throw new IndexOutOfBoundsException();
		}
//		if(pos > this.size() - 1 || pos < 0){
//			throw new IndexOutOfBoundsException();
//		}
		return this.root.get(pos);
	}

	/**
	 * MILESTONE 1
	 * @return the height of this tree
	 */
	public int height() {
		return this.root.height() - 1;
	}

	/**
	 * MILESTONE 2
	 * @return the number of nodes in this tree
	 */
	public int size() {
		return this.root.size();
	}
	
	
	/**
	 * MILESTONE 2
	 * @param pos
	 *            position of character to delete from this tree
	 * @return the character that is deleted
	 * @throws IndexOutOfBoundsException
	 */
	public char delete(int pos) throws IndexOutOfBoundsException {
		// Implementation requirement:
		// When deleting a node with two children, you normally replace the
		// node to be deleted with either its in-order successor or predecessor.
		// The tests assume assume that you will replace it with the
		// *successor*.
		if(pos < 0 || pos > this.size() - 1){
			throw new IndexOutOfBoundsException();
		}
		this.currentSize --;
		return this.root.delete(pos); // replace by a real calculation.
	}

	/**
	 * MILESTONE 3, EASY
	 * This method operates in O(length*log N), where N is the size of this
	 * tree.
	 * 
	 * @param pos
	 *            location of the beginning of the string to retrieve
	 * @param length
	 *            length of the string to retrieve
	 * @return string of length that starts in position pos
	 * @throws IndexOutOfBoundsException
	 *             unless both pos and pos+length-1 are legitimate indexes
	 *             within this tree.
	 */
	public String get(int pos, int length) throws IndexOutOfBoundsException {
		if(pos < 0 || pos + length - 1 >= this.size()){
			throw new IndexOutOfBoundsException();
		}
		StringHolder toReturn = new StringHolder("");
		int current = pos;
		while(current < pos + length){
			toReturn.setData(toReturn.getData() + this.get(current));
			current++;
		}
		return toReturn.getData();
	}

	/**
	 * MILESTONE 3, MEDIUM - SEE PAPER REFERENCED IN SPEC FOR ALGORITHM!
	 * Append (in time proportional to the log of the size of the larger tree)
	 * the contents of the other tree to this one. Other should be made empty
	 * after this operation.
	 * 
	 * @param other
	 * @throws IllegalArgumentException
	 *             if this == other
	 */
	//TODO
	public void concatenate(EditTree other) throws IllegalArgumentException {
		if(this == other){
			throw new IllegalArgumentException();
		}
		if(this.isEmpty()){
			this.root = other.root;
			other.root = NULL_NODE;
			return;
		}
		else if(other.isEmpty()){
			return;
		}
		int bigger;//1 = this, -1 = other, 0 = same
		if(this.height() > other.height()){
			bigger = 1;
		}
		else if(this.height() < other.height()){
			bigger = -1;
		}
		else{
			bigger = 0;
		}
		this.currentSize += other.size();
		if(bigger == 1){
			Node farLeft = new Node(other.delete(0), this);
			farLeft.right = other.root;
			other.root.parent = farLeft;
			int otherHeight = other.height();
			System.out.println(this.root.insertAtHeightRight(farLeft, otherHeight, this.root.height()));
			other.root = NULL_NODE;
		}
		else if(bigger == -1){
			Node farRight = new Node(this.delete(this.size() - 1), this);
			farRight.left = this.root;
			this.root.parent = farRight;
			int thisHeight = this.height();
			System.out.println(other.root.insertAtHeightLeft(farRight, thisHeight, other.root.height()));
			this.updateRoot(other.root);
			other.root = NULL_NODE;
		}
		else{
			Node farLeft = new Node(other.delete(0), this);
			farLeft.right = other.root;
			farLeft.left = this.root;
			this.root.parent = farLeft;
			other.root.parent = farLeft;
			farLeft.parent = NULL_NODE;
			if(farLeft.right.height() == farLeft.left.height() + 1){
				farLeft.balance = Code.RIGHT;
			}
			else if(farLeft.right.height() == farLeft.left.height() - 1){
				farLeft.balance = Code.LEFT;
			}
			else if(farLeft.right.height() == farLeft.left.height()){
				farLeft.balance = Code.SAME;
			}
			else{
				System.out.println("This shouldn't happen...");
			}
			farLeft.rank = farLeft.left.size();
			this.updateRoot(farLeft);
			other.root = NULL_NODE;
		}
	}

	/**
	 * MILESTONE 3: DIFFICULT
	 * This operation must be done in time proportional to the height of this
	 * tree.
	 * 
	 * @param pos
	 *            where to split this tree
	 * @return a new tree containing all of the elements of this tree whose
	 *         positions are >= position. Their nodes are removed from this
	 *         tree.
	 * @throws IndexOutOfBoundsException
	 */
	//TODO
	
	//I'm making this inefficient because I honestly have no clue how to do it with
	//how I've implemented my other code. They really ought to have told us to use stacks
	//from the start so once you get to this step you aren't totally screwed.
	public EditTree split(int pos) throws IndexOutOfBoundsException {
		if(pos < 0 || pos > this.size() - 1){
			throw new IndexOutOfBoundsException();
		}
		if(pos == this.currentSize){
			return new EditTree(this.delete(pos));
		}
		String original = this.toString();
		String start = original.substring(0, pos);
		String end = original.substring(pos);
		EditTree t1 = new EditTree(start);
		EditTree t2 = new EditTree(end);
		this.root = t1.root;
		return t2;
//		EditTree toReturn = new EditTree();
//		while(true){
//			try{
//				char toAdd = this.delete(pos);
//				toReturn.add(toAdd);
//				System.out.println(this.toDebugString());
//				System.out.println(toReturn.toDebugString());
//			}
//			catch(IndexOutOfBoundsException e){
//				System.out.println("Caught the exception");
//				break;
//			}
//		}		
//		return toReturn; // replace by a real calculation.
	}

	/**
	 * MILESTONE 3: JUST READ IT FOR USE OF SPLIT/CONCATENATE
	 * This method is provided for you, and should not need to be changed. If
	 * split() and concatenate() are O(log N) operations as required, delete
	 * should also be O(log N)
	 * 
	 * @param start
	 *            position of beginning of string to delete
	 * 
	 * @param length
	 *            length of string to delete
	 * @return an EditTree containing the deleted string
	 * @throws IndexOutOfBoundsException
	 *             unless both start and start+length-1 are in range for this
	 *             tree.
	 */
	public EditTree delete(int start, int length)
			throws IndexOutOfBoundsException {
		if (start < 0 || start + length >= this.size())
			throw new IndexOutOfBoundsException(
					(start < 0) ? "negative first argument to delete"
							: "delete range extends past end of string");
		EditTree t2 = this.split(start);
		EditTree t3 = t2.split(length);
		this.concatenate(t3);
		return t2;
	}

	/**
	 * MILESTONE 3
	 * Don't worry if you can't do this one efficiently.
	 * 
	 * @param s
	 *            the string to look for
	 * @return the position in this tree of the first occurrence of s; -1 if s
	 *         does not occur
	 */
	public int find(String s) {
		return this.find(s, 0);
	}

	/**
	 * MILESTONE 3
	 * @param s
	 *            the string to search for
	 * @param pos
	 *            the position in the tree to begin the search
	 * @return the position in this tree of the first occurrence of s that does
	 *         not occur before position pos; -1 if s does not occur
	 */
	public int find(String s, int pos) {
		if(pos < 0){
			throw new IndexOutOfBoundsException();
		}
		if(pos + s.length() - 1 >= this.size()){
			return -1;
		}
		if(s.equals("")){
			return pos;
		}
		int currentPos = pos;
		int stringLength = s.length();
		while(currentPos + stringLength <= this.size()){
			if(this.get(currentPos, stringLength).equals(s)){
				return currentPos;
			}
			currentPos++;
		}	
		return -1;
	}

	/**
	 * @return The root of this tree.
	 */
	public Node getRoot() {
		return this.root;
	}

	public void updateRoot(Node b) {
		this.root = b;
	}
}
