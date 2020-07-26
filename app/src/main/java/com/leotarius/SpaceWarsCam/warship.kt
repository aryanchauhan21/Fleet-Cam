package com.leotarius.SpaceWarsCam

sealed class Warship {
    abstract val rotationDegree: Float

    object StarDestroyer: Warship(){
        override val rotationDegree: Float
            get() = 180f
    }

    object TieSilencer: Warship(){
        override val rotationDegree: Float
            get() = 90f
    }

    object XWing: Warship(){
        override val rotationDegree: Float
            get() = -90f
    }
}