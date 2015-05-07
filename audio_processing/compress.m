function Xc = compress(x, b, c)

[m n] = size(x); %Finds size of X

m_rem = mod(m, (b/2)); %Finds remainder of x/(.5*b)
x = padarray(x,[((b/2) - m_rem) 0], 'post'); %Pads array X to make it 
% divisible by b/2
 
win = kbdwin(b, 2.5); %Window of size b

    function C = window(x)
        [m n] = size(x); %Gets size of matrix x
        y=reshape(x,(b/2),(2*m/b)); 
        z=[y(:,1:end-1);y(:,2:end)]; %Reshapes the matrix so that the 
        %columns are the blocks that will be windowed
        window = repmat(win, 1, size(z, 2)); %Reshapes win (using repmat)
        % to get it to be the size of z
        mod_win = z .* window; %Multiplies the matrix by window
        C = mod_win;
    end

C = window(x); %Windows x


X = mdct4(C); %Transforms windowed data

[j k] = size(X); %Determines size of x
X = reshape(X, 1, j*k); %Reshapes to 1 x j*k
[sorted, index] = sort(abs(X), 'ascend'); %Sorts absolute value of 
%matrix in ascending order and gets index of sorted matrix
in = j*k - c*j*k; %Finds the index to which the matrix will be zeroed
minindex = index(1:floor(in)); %Returns index of all number uptil in
X(minindex) = 0; %Sets all of these values at index to 0
X = reshape(X, j, k); %Reshapes back to jxk matrix
Xc = X;
end

