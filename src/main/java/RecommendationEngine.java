import com.jasongoodwin.monads.Try;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.mllib.recommendation.ALS;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.Rating;
import org.apache.spark.sql.SQLContext;
import scala.Tuple2;

public class RecommendationEngine {
    public static final String moviesPath = "/Users/chandu/Downloads/ml-1m/movies.dat";
    public static final String usersPath = "/Users/chandu/Downloads/ml-1m/users.dat";
    public static final String ratingsPath = "/Users/chandu/Downloads/ml-1m/ratings.dat";

    public static void main(String[] args) {
        JavaSparkContext jsc = new JavaSparkContext("local", "Recommendation Engine");
        SQLContext sqlContext = new SQLContext(jsc);

        JavaRDD<Movie> movieRDD = jsc.textFile(moviesPath).map(line -> {
            final String[] movieArray = line.split("::");
            Integer movieId = Integer.parseInt(Try.ofFailable(() -> movieArray[0]).orElse("-1"));
            return new Movie(movieId, movieArray[1], movieArray[2]);
        }).cache();

        JavaRDD<User> userRDD = jsc.textFile(usersPath).map(line -> {
            String[] userArr = line.split("::");
            Integer userId = Integer.parseInt(Try.ofFailable(() -> userArr[0]).orElse("-1"));
            Integer age = Integer.parseInt(Try.ofFailable(() -> userArr[2]).orElse("-1"));
            Integer occupation = Integer.parseInt(Try.ofFailable(() -> userArr[3]).orElse("-1"));
            return new User(userId, userArr[1], age, occupation, userArr[4]);
        }).cache();

        JavaRDD<Rating> ratingRDD = jsc.textFile(ratingsPath).map(line -> {
            String[] ratingArr = line.split("::");
            Integer userId = Integer.parseInt(Try.ofFailable(() -> ratingArr[0]).orElse("-1"));
            Integer movieId = Integer.parseInt(Try.ofFailable(() -> ratingArr[1]).orElse("-1"));
            Double rating = Double.parseDouble(Try.ofFailable(() -> ratingArr[2]).orElse("-1"));
            return new Rating(userId, movieId, rating);
        }).cache();


        JavaRDD<Rating>[] ratingSplits = ratingRDD.randomSplit(new double[] { 0.8, 0.2 });

        JavaRDD<Rating> ratingTrainingRDD = ratingSplits[0].cache();
        JavaRDD<Rating> ratingTestRDD = ratingSplits[1].cache();

        //System.out.println("Test Data Count ------- " + ratingTestRDD.count());

        MatrixFactorizationModel model = ALS.train(JavaRDD.toRDD(ratingTrainingRDD), 5, 10, 0.01);

        JavaPairRDD<Integer, Integer> testUserProductRDD = ratingTestRDD.mapToPair(rating ->
                new Tuple2<>(rating.user(), rating.product()));

        JavaRDD<Rating> predictionsForTestRDD = model.predict(testUserProductRDD);

        //System.out.println("Predictions Count ------- " + predictionsForTestRDD.count());

        System.out.println("Test predictions");
        predictionsForTestRDD.take(10).stream().forEach(rating -> {
            System.out.println("Product id : " + rating.product() + "-- Rating : " + rating.rating());
        });

        JavaPairRDD<Tuple2<Integer, Integer>, Double> predictionsKeyedByUserProductRDD = predictionsForTestRDD.mapToPair(r ->
                new Tuple2<>(new Tuple2<>(r.user(), r.product()),r.rating()));

        //System.out.println(predictionsKeyedByUserProductRDD.count());

        JavaPairRDD<Tuple2<Integer, Integer>, Double> testKeyedByUserProductRDD = ratingTestRDD.mapToPair(r ->
                new Tuple2<>(new Tuple2<>(r.user(), r.product()),r.rating()));

        //System.out.println(testKeyedByUserProductRDD.count());

        JavaRDD<Tuple2<Double,Double>> testAndPredictionsJoinedRDD  = testKeyedByUserProductRDD.join(predictionsKeyedByUserProductRDD).values();

        //System.out.println("Test and Predictions Count -------- " + testAndPredictionsJoinedRDD.count());

        double MSE = testAndPredictionsJoinedRDD.mapToDouble(pair -> {
            double err = pair._1() - pair._2();
            return err * err;
        }).mean();

        System.out.println("Mean Squared Error = " + MSE);


    }
}
