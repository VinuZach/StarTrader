package com.example.startraders.models

data class Denominations(val denomination:String,val count:String)

fun MutableList<Denominations>.filterByDenomination(denomination: String) = this.filter { it.denomination.equals(denomination)  }