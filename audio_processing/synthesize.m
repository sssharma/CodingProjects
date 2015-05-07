function xr = synthesize(Xc)

[m n] = size(full(Xc));
cell = num2cell(full(Xc), 1); %Converts the columns of full(Xc) to cells
cells = cellfun(@(x) imdct4(x), cell, 'UniformOutput', false); 
%Applies the imdct4 to each cell (column)

win = kbdwin(2*m, 2.5); %Gets window of size 2*m

cells = cellfun(@(x) x.*win, cells, 'UniformOutput', false); 
%Multiplies each column (block) by the window

Xc = cell2mat(cells); %Converts the cell array back to matrix
[m n] = size(Xc);
bot = Xc((m/2)+1:m, 1:n-1); %Finds the bottom 1/2 Xc from columns 1:n-1
top = Xc(1:(m/2), 2:n);%Finds the top 1/2 Xc from columns 1:n-1
comb = reshape(bot+top, (m/2)*(n-1), 1); %Combines bot and top, as these 
% 2 portions represent overlap of the original matrix
xr = cat(1, Xc(1:(m/2), 1), comb); %Concatenates comb with the top 1/2 of
% column 1 of Xc (which does not overlap with anything)
xr = cat(1, xr, Xc((m/2)+1:m, n));%Concatenates comb with the bottom 1/2 of
% column n of Xc (which does not overlap with anything)



end