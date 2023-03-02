package entity

/**
 * An object of data class Card stores all relevant information about a card
 *  @param [id] tells the CardLoader which image it is.
 *  @param [routes] Stores the channels of the map.
 *  @param [angle] says how many degrees the map has been rotated.
 *  @param [cardType] stores the type of card.
 */
data class Card(val id:Int, val cardType:CardType, val routes:HashMap<Int,Int>, var angle:Int)  {

    //The maps store the entrances to the neighboring cards depending on the given entrance and the angle
    private val map1 = mapOf(0 to 7,1 to 6 ,2 to 1,3 to 0,4 to 3,5 to 2,6 to 5, 7 to 4)
    private val map2 = mapOf(0 to 1 ,1 to 0,2 to 3,3 to 2,4 to 5,5 to 4 ,6 to 7,7 to 6)
    private val map3 = mapOf(0 to 3 ,1 to 2,2 to 5,3 to 4,4 to 7,5 to 6 ,6 to 1,7 to 0)
    private val map4 = mapOf(0 to 5 ,1 to 4,2 to 7,3 to 6,4 to 1,5 to 0 ,6 to 3,7 to 2)
    private val map = mapOf(90 to map1, 180 to map2, 270 to map3, 0 to map4)

    /**
     * This method gives the way (between the cards)
     *  from entrance to entrance
     */
    fun getWay(enterId : Int) : Int{
        val angleMap = map[angle]
        if (angleMap != null && enterId >= 0 && enterId < 8){
            val newEnterId = (enterId + (8 - 2*(angle / 90))) % 8
            return angleMap[routes[newEnterId]]!!
        }
        else
        {
            throw IllegalStateException()
        }
    }
}