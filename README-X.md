<!-- This file is rendered by https://github.com/BlvckBytes/readme_helper -->

# Equation Corrector (Failure!)

Sometimes, one just "wastes" time, misleading oneself down a dead end, walking right past the solution. This was one of these cases, and I'd like to hereby immortalize it, as it still provides knowledge and thus value to me. (Plus, I might want to get back to it in the future!)

<!-- #toc -->

## Introduction

A few days ago, I stumbled upon a seemingly easy quiz on [spektrum.de](https://www.spektrum.de), which has been formulated in the year 2023 by `John Kruse` and states the following:

```txt
2 + 7 = 1 * 3

Add exactly four threes to this equation, so that it becomes correct.
```

Usually, I **hate** vague "rules" like these (for a good reason!), but that didn't keep me from really wanting to know the solution without clicking the `Reveal`-button. My interpretation of it was that threes may only be added as leading or trailing attachments to already existing numbers and that the introduction of additional operators is strictly forbidden. *How wrong I was.*

Sadly, I haven't yet reached a level of mathematical proficiency which would allow me to formally either disprove that there's a solution or to find a set of possible solutions, and thus I made use of my ultimate tool: programming! *Just brute-force it.*

## Generating Permutations

It should be pretty obvious, that there are **many** possible combinations. The requirement that exactly four threes are to be added to the equation is cutting them down by a great deal already though. Still, I'm not looking forward to writing these by hand, so let's generate them. This goal can be accomplished most understandably when the objective is split into three steps:

### Threes Distribution Per Column

I define a column to be one variable in the generalized equation `a + b = c * d`. So, if there are `n` threes in a column, there are `m` different possibilities in which these can be distributed.

The following example shows all five possible distributions when having four threes on one column:

```txt
3333a
333a3
33a33
3a333
a3333
```

In general, for `n` threes, there will be `m = n + 1` possibilities, since the variable can be at either one of these `n` digit's positions, or be appended (/prepended, depending on the point of reference). Generating these permutations is the most trivial, applying them is a bit more work.

Let's define the length in digits of a number `a` as follows: `len(a) = floor(log10(a)) + 1`. The output of the base-10 logarithm will be a floating point value `x`, as in `10^x = a`. So for all numbers `<10`, it'll be `<1`, for `<100` it'll be `<2`, for `<1000` it'll be `<3` and so on and so forth. Flooring it's result and adding one will yield the number of digits (`len`) for the whole range of numbers in `[10^(len - 1);10^len - 1]`.

Appending the digits of `x` to variable `a` means that the place-value of `a`'s digits need to be shifted to the left by `len(x)`, and `x` can just be added into the new zero-spots: `10^len(x) * a + x`. To prepend the digits of `x`, the place-value of it's digits need to be increased so that the number starts out with said digits and has as many trailing zeroes as there are digits in `a`, so they can then just be added together, analogous to appending a digit: `10^len(a) * x + a`. To both append the digits of `x` and prepend the digits of `y` to variable `a`, the above steps just need to be completed in order, where the second step needs to take the changes of the first into account.

### Number Of Threes Per Column

There can be `[0;4]` added threes in a column, with the constraint that the total amount of added threes in all columns sums up to be four. To me, the most expressive way of generating these permutations is recursion, where each column starts out at zero. The last column starts to count up to four. Once it reaches it's maximum, the column returns, and it's predecessor increases it's count by one, then invokes the successor again with one less as it's maximum and a template that has a one in the second to last column. The child counts up again, returns, parent increases, and so on and so forth. This pattern is completed until the first-most column reaches the maximum value. This algorithm generates all possible combinations, even those that do not sum up to four, so these need to be eliminated while generating.

### Cross Joining Permutations

With each permutation of [Number Of Threes Per Column](#number-of-threes-per-column), multiple equations will arise, as there are multiple ways to distribute the threes per each column, as per [Threes Distribution Per Column](#threes-distribution-per-column). All possible permutations of each individual column in this numbers-of-threes permutation need to be cross-joined, so that each possible constellation is encountered once. This way, a (relative to it's inputs) metric ton of final permutations are generated from two rather simple and much smaller permutations, ensuring the existence of all possible cases (hopefully!).

## Failure

Who would've guessed it... the best difference between the equation's left- and right-hand-side throughout all equations is `3`.

```txt
Minimum delta accomplished: 3, with:

323 + 7 = 1 * 333 [(1, 1), (0, 0), (0, 0), (0, 2)]
323 + 7 = 1 * 333 [(1, 1), (0, 0), (0, 0), (1, 1)]
323 + 7 = 1 * 333 [(1, 1), (0, 0), (0, 0), (2, 0)]

Tried 330 permutations in total
```

There is no way to satisfy this equality by appending or prepending threes in any way possible. In the end, I finally gave in and viewed the solution:

```txt
2^3 + 7^3 = 13 · 3^3
```

You've got to be kidding me... exponentiation is an allowed operation in this puzzle. Am I really the only one who interpreted the constraints in the above described way?