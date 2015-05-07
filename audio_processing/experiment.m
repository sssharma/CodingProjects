function [] = experiment(filename, b, cRatio)

[y, Fs] = wavread(filename); %Reads the wav file

y = sum(y, 2); %Sums the matrix along the column

y = padarray(y, [b/2 0]); %Pads the array pre- and post- with b/2 0's 

Xc = compress(y, b, cRatio); %Compresses y with block b and compression
%ratio c

figure, imagesc(Xc); %Plots the compressed audio
set(gca,'YDir', 'normal'); %Alters settings so that the frequency increases
% over the y-axis

xr = synthesize(Xc); %Synthesizes Xc into xr

soundsc(y, Fs); %Plays the original sound
pause(1); %Pauses for one second
soundsc(xr, Fs); %Plays the synthesized sound
pause(1); %Pauses 1 second


end