filename = 'arribba.wav';
experiment(filename, 1024, .990);
experiment(filename, 1024, .5);
experiment(filename, 1024, .2);

experiment(filename, 16384, .001); %Incoherent
experiment(filename, 16384, .2);

experiment(filename, 64, .001);
experiment(filename, 64, .2);


experiment(filename, 1024, .025);
% This is probably the lowest compression ratio at which the audio is still
% discernible in it's contents. At higher block sizes, there is excessive
% noise introduced and at lower block sizes it is too staccato - at lower
% block sizes, it is unintelligible. 



filename = 'voice.wav';

experiment(filename, 1024, .990);
experiment(filename, 1024, .5);
experiment(filename, 1024, .01);

experiment(filename, 16384, .001);
experiment(filename, 16384, .01);

experiment(filename, 64, .001);
experiment(filename, 64, .01);

experiment(filename, 4096, .005);
% This is probably the smallest the compression ratio can be for an
% intelligible compressed sound. At higher block sizes there is excessive
% noise (echo) introduced and at lower block sizes the words become
% intelligible as data is lost. At lower compression ratio, the words
% become excessively muffled. 

%The amount you can compress the human voice depends a lot on the type of
%sounds - we can compress 'voice.wav' a lot because the tonal range is not
%very wide. However, we cannot for 'arriba.wav' as there is a large range
%of sounds and it achieves very high pitches. 

filename = 'birds_sec.wav';

experiment(filename, 1024, .990);
experiment(filename, 1024, .5);
experiment(filename, 1024, .01);

experiment(filename, 16384, .001);
experiment(filename, 16384, .01); %Pre- and post- echo heard distinctively

experiment(filename, 64, .001);
experiment(filename, 64, .01);

%This file can be compressed a lot - even though there is echo
%surrounding the sounds, since the echo is at the same pitch as the sounds
%itself, it meshes well. At experiment(filename, 16384, .001) it is still
%quite intelliglbe - at lower block sizes, the pitch quality of the sound
%is lost (at b = 64, the pitch is drastically lower). 


filename = 'nin_ghosts1_piano_clip.wav';

experiment(filename, 1024, .990);
experiment(filename, 1024, .5);
experiment(filename, 1024, .001);

experiment(filename, 16384, .001);
experiment(filename, 16384, .01);

experiment(filename, 64, .001);
experiment(filename, 64, .01);

%The ghosts piano can be compressed a lot, since sounds and tones are not 
%distinctive, but rather blend into each other. Any pre- and post- echoes 
%are not audible as there is a natural echo in the original audio itself.
%For ghosts piano, the best way to compress it is with a large block size
%or an inordinate amount of data is lost. 


%The synthesized version is significantly longer than the synthesized
%version. 

%The smaller the background noise, the more staccato it is and the more
%information that is lost. The bigger the block size, the more the
%background noise that is introduced. 

%Very crisp sounds will have more audible artifacts as there will be
%background noise and pre- and post-echoes surrounding the sound, and 
%introduces double speak.

