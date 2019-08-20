package sheetmusic.noteidentifiertrainer;

import org.apache.commons.lang3.ArrayUtils;
import org.nd4j.linalg.util.ArrayUtil;

import javax.sound.midi.*;
import java.util.*;

public class MyMIDIPlayer {

    final String[] NOTES = {"Cn", "B#", "C#", "Db","Dn", "Dn", "D#", "Eb","En", "Fb", "E#", "Fn",
        "F#", "Gb", "Gn", "Gn", "G#", "Ab","An", "An", "A#", "Bb","Bn", "Cb"};

    public static void main(String[] args) {

        System.out.println("Enter the number of notes to be played: ");
        Scanner in = new Scanner(System.in);
        int numOfNotes = in.nextInt();

        MyMIDIPlayer player = new MyMIDIPlayer();
        //player.setUpPlayer(numOfNotes);
    }

    public void setUpPlayer(ArrayList<Symbol> symbols) {

        try {

            // A static method of MidiSystem that returns
            // a sequencer instance.
            Sequencer sequencer = MidiSystem.getSequencer();
            sequencer.open();

            // Creating a sequence.
            Sequence sequence = new Sequence(Sequence.PPQ, 4);

            // PPQ(Pulse per ticks) is used to specify timing
            // type and 4 is the timing resolution.

            // Creating a track on our sequence upon which
            // MIDI events would be placed
            Track track = sequence.createTrack();

            String[] notes = {"An","En", "Dn", "Cn", "Dn", "En", "En", "En","Dn","Dn","Dn","En","Gn","Gn",
                "En", "Dn", "Cn", "Dn", "En", "En", "En","En","Dn","Dn","En","Dn","Cn"};

            // Adding some events to the track
            for (int i = 5; i < symbols.size(); i += 1) {

                if (symbols.get(i/8).id.contains("note")) {
                    // Add Note On event
                    track.add(makeEvent(144, 1,
                        getNote(4 + symbols.get(i/8).octave(), symbols.get(i/8).pitch()),
                        100, i));

                    // Add Note Off event
                    track.add(makeEvent(128, 1,
                        getNote(4 + symbols.get(i/8).octave(), symbols.get(i/8).pitch()),
                        100, i + (int) (symbols.get(i/8).length * 4)));
                } else {
                    track.add(makeEvent(144, 1,
                        getNote(1, "Cn"),
                        100, i*4));

                    // Add Note Off event
                    track.add(makeEvent(128, 1,
                        getNote(1, "Cn"),
                        100,i*4 + (int) (symbols.get(i/8).length * 4)));
                }
            }
            System.out.println("Song made :)");

            // Setting our sequence so that the sequencer can
            // run it on synthesizer
            sequencer.setSequence(sequence);

            // Specifies the beat rate in beats per minute.
            sequencer.setTempoInBPM(220);

            // Sequencer starts to play notes
            sequencer.start();

            while (true) {

                // Exit the program when sequencer has stopped playing.
                if (!sequencer.isRunning()) {
                    sequencer.close();
                    System.exit(1);
                }
            }
        }
        catch (Exception ex) {

            ex.printStackTrace();
        }
    }

    public int getNote(int octave, String pitch){
        int intOctave =  octave*7;
        int intPitch = ArrayUtils.indexOf(NOTES,pitch)/2;
        return intOctave + intPitch;
    }

    public MidiEvent makeEvent(int command, int channel,  int note, int velocity, int tick) {

        MidiEvent event = null;

        try {

            // ShortMessage stores a note as command type, channel,
            // instrument it has to be played on and its speed.
            ShortMessage a = new ShortMessage();
            a.setMessage(command, channel, note, velocity);

            // A midi event is comprised of a short message(representing
            // a note) and the tick at which that note has to be played
            event = new MidiEvent(a, tick);
        }
        catch (Exception ex) {

            ex.printStackTrace();
        }
        return event;
    }
}
