package org.benf.cfr.reader.entities.exceptions;

import org.benf.cfr.reader.bytecode.analysis.parse.utils.BlockIdentifier;
import org.benf.cfr.reader.entities.ConstantPool;
import org.benf.cfr.reader.util.ListFactory;

import java.util.List;

/**
 * Created:
 * User: lee
 * Date: 21/05/2012
 */
public class ExceptionGroup {

    private final short bytecodeIndexFrom;        // [ a
    private short byteCodeIndexTo;          // ) b    st a <= x < b
    private short minHandlerStart = Short.MAX_VALUE;
    private List<Entry> entries = ListFactory.newList();
    private final BlockIdentifier tryBlockIdentifier;
    private final ConstantPool cp;

    public ExceptionGroup(short bytecodeIndexFrom, BlockIdentifier blockIdentifier, ConstantPool cp) {
        this.bytecodeIndexFrom = bytecodeIndexFrom;
        this.tryBlockIdentifier = blockIdentifier;
        this.cp = cp;
    }

    public void add(ExceptionTableEntry entry) {
        if (entry.getBytecodeIndexHandler() == entry.getBytecodeIndexFrom()) return;
        if (entry.getBytecodeIndexHandler() < minHandlerStart) minHandlerStart = entry.getBytecodeIndexHandler();
        this.entries.add(new Entry(entry));
        if (entry.getBytecodeIndexTo() > byteCodeIndexTo) byteCodeIndexTo = entry.getBytecodeIndexTo();
//        if (byteCodeIndexTo > minHandlerStart) byteCodeIndexTo = minHandlerStart;
    }

    public List<Entry> getEntries() {

        return entries;
    }

    public short getBytecodeIndexFrom() {
        return bytecodeIndexFrom;
    }

    public short getByteCodeIndexTo() {
        return byteCodeIndexTo;
    }

    public BlockIdentifier getTryBlockIdentifier() {
        return tryBlockIdentifier;
    }

    @Override
    public String toString() {
        return "[egrp : " + tryBlockIdentifier + " [" + bytecodeIndexFrom + "->" + byteCodeIndexTo + ")]";
    }

    public class Entry {
        private final ExceptionTableEntry entry;

        public Entry(ExceptionTableEntry entry) {
            this.entry = entry;
        }

        public short getBytecodeIndexTo() {
            return entry.getBytecodeIndexTo();
        }

        public short getBytecodeIndexHandler() {
            return entry.getBytecodeIndexHandler();
        }

        public boolean isJustThrowable() {
            short type = entry.getCatchType();
            return type == 0;
        }

        public String getTypeName() {
            short type = entry.getCatchType();
            String name;
            if (type == 0) {
                name = "java.lang.Throwable";
            } else {
                name = cp.getClassEntry(type).getTypeInstance(cp).toString();
            }
            return name;
        }

        @Override
        public String toString() {
            short type = entry.getCatchType();
            String name = getTypeName();
            return ExceptionGroup.this.toString() + " " + name;
        }
    }
}
