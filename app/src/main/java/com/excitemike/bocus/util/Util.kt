/**
 * Useful things that didn't have an otherwise obvious place to put them
 */
package com.excitemike.bocus.util

/** true if the desiredBits are all 1 in bits */
fun checkFlags(bits:Int, desiredBits:Int): Boolean {
    return desiredBits == (bits and desiredBits)
}