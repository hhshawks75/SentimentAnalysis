/**This class is the movieReview class object where it is a node
 * this node will hold 4 characteristics and getters
 *
 *
 *
 *
 */

package movieReviewClassification;


import java.io.Serializable;

public class MovieReview implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The id of the review (e.g. 2087).
     */
    private final int id;

    /**
     *  The text of the review.
     */
    private final String text;

    /**
     * The predicted polarity of the Movie Review (0 = negative, 1 = positive).
     */
    private int predictedPolarity;

    /**
     * The ground truth polarity of the review (0 = negative, 1 = positive, 2 = unknown).
     */
    private final int realPolarity;


    public  MovieReview(int id, String text, int realPolarity)
    {
        this.id = id;
        this.text = text;
        this.realPolarity = realPolarity;
        this.predictedPolarity = 0; // Set a default value. To be changed later.
    }
    public int getId()
    {
        return id;
    }
    public String getText()
    {
        return text;
    }
    public int getPredictedPolarity()
    {
        return predictedPolarity;
    }
    public void setPredictedPolarity(int predictedPolarity)
    {
        this.predictedPolarity = predictedPolarity;
    }
    public int getRealPolarity()
    {
        return realPolarity;
    }
    public String toString()
    {
        return  "\n"+
                "ID: "+ id+"  |  "
                +"TEXT: "+text.substring(0, 50)+"  |  "
                +"REAL POLARITY: " +realPolarity+"  |  "
                +"PREDICTED POLARITY: "+predictedPolarity+"]\n";
    }



}
