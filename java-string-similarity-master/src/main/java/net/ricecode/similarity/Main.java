package net.ricecode.similarity;

public class Main {

	public static void main(String[] args) {
		SimilarityStrategy strategy = new LevenshteinDistanceStrategy();
		String target = "McDonalds";
		String source = "McDon";
		StringSimilarityService service = new StringSimilarityServiceImpl(strategy);
		double score = service.score(source, target); // Score is 0.90
        System.out.println("score is "+score);
	}

}
