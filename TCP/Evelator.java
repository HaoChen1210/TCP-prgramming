package comp3331;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Evelator {
	public int solution(int[] A, int[] B, int M, int X, int Y) {
		int i = 0;
		int totalP = 0;
		int totalW = 0;
		int total = 0;
		// ArrayList<Integer> records = new ArrayList<>();
		Set<Integer> records = new HashSet<>();
		for (i = 0; i < A.length; i++) {
			totalP++;
			totalW += A[i];
			if (B[i] <= M) {
				if (totalP > X || totalW > Y) {
					total += records.size();
					total++;
					i--;
					totalW = 0;
					totalP = 0;
					records.clear();					
				} else {
					records.add(B[i]);
				}
			}
			else{
				;
			}

		}
		total = records.size() + total + 1;
		return total;

		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {

		/*
		 * int X[] = {60, 80, 40}; int Y[] = {2, 3, 5}; int people = 2; int
		 * weight = 200; int M = 5;
		 */
		/*
		 * int A[] = {40, 40, 100,80,20}; int B[] = {3, 3, 2, 2, 3}; int X = 5;
		 * int Y = 200; int M = 3;
		 * 
		 * System.out.println(new solution().solution(A, B, M,X, Y));
		 */
		int A[] = { 60, 80, 40 };
		int B[] = { 2, 3, 5 };
		int people = 2;
		int weight = 200;
		int M = 5;
		System.out.println(new Evelator().solution(A, B, M, people, weight));

	}

}
