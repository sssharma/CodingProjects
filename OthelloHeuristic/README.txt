Describe how and what each group member contributed for the past two weeks. If you are solo, these points will be part of the next section.
We both contributed equally to the project - we worked together to create a heuristic that integrated mobility, frontiers and strategic positioning and that effectively beat the given computer models. We also worked together on the coding - writing and debugging the code together. 

Document the improvements that your group made to your AI to make it tournament-worthy. Explain why you think your strategy(s) will work. Feel free to discuss any ideas were tried but didn't work out.

We used alpha-beta pruning to make our recursive algorithm able to have a deep depth in a feasible time. We set it up such that it runs to depth 6 for the begging and middle game. For the endgame, when there are 12 boxes left open, we set the depth to 12 so as to determine the end strategies.
The main heuristic functions used for the algorithm is mobility, frontiers and strategic position - we implement these 3 standards for both sides (playSide and oppSide.) 
The strategy that we used works because our strategy focuses on reducing the mobility of the oppSide and increasing it of playSide such that we have a greater range of movement compared to the other player. By increasing the oppSide's frontiers and reducing our frontiers we are able to reduce the number of plays of the oppSide and therefore able to protect a greater number of playSide's disks. Finally, using a strategic position heuristic means that we are able to value certain more stable and strategic positions (such as corners) with a high value and others (such as the spaces right next to corners) with a low value.




