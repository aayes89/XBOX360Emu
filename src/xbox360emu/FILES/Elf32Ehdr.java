package xbox360emu.FILES;

/**
 *
 * @author Slam
 */
public class Elf32Ehdr {

    byte[] e_ident = new byte[16];
    int e_type;
    int e_machine;
    int e_version;
    int e_entry;
    int e_phoff;
    int e_shoff;
    int e_flags;
    int e_ehsize;
    int e_phentsize;
    int e_phnum;
    int e_shentsize;
    int e_shnum;
    int e_shstrndx;

    public Elf32Ehdr() {
    }

    public Elf32Ehdr(int e_type, int e_machine, int e_version, int e_entry, int e_phoff, int e_shoff, int e_flags, int e_ehsize, int e_phentsize, int e_phnum, int e_shentsize, int e_shnum, int e_shstrndx) {
        this.e_type = e_type;
        this.e_machine = e_machine;
        this.e_version = e_version;
        this.e_entry = e_entry;
        this.e_phoff = e_phoff;
        this.e_shoff = e_shoff;
        this.e_flags = e_flags;
        this.e_ehsize = e_ehsize;
        this.e_phentsize = e_phentsize;
        this.e_phnum = e_phnum;
        this.e_shentsize = e_shentsize;
        this.e_shnum = e_shnum;
        this.e_shstrndx = e_shstrndx;
    }

    public byte[] getE_ident() {
        return e_ident;
    }

    public void setE_ident(byte[] e_ident) {
        this.e_ident = e_ident;
    }

    public int getE_type() {
        return e_type;
    }

    public void setE_type(int e_type) {
        this.e_type = e_type;
    }

    public int getE_machine() {
        return e_machine;
    }

    public void setE_machine(int e_machine) {
        this.e_machine = e_machine;
    }

    public int getE_version() {
        return e_version;
    }

    public void setE_version(int e_version) {
        this.e_version = e_version;
    }

    public int getE_entry() {
        return e_entry;
    }

    public void setE_entry(int e_entry) {
        this.e_entry = e_entry;
    }

    public int getE_phoff() {
        return e_phoff;
    }

    public void setE_phoff(int e_phoff) {
        this.e_phoff = e_phoff;
    }

    public int getE_shoff() {
        return e_shoff;
    }

    public void setE_shoff(int e_shoff) {
        this.e_shoff = e_shoff;
    }

    public int getE_flags() {
        return e_flags;
    }

    public void setE_flags(int e_flags) {
        this.e_flags = e_flags;
    }

    public int getE_ehsize() {
        return e_ehsize;
    }

    public void setE_ehsize(int e_ehsize) {
        this.e_ehsize = e_ehsize;
    }

    public int getE_phentsize() {
        return e_phentsize;
    }

    public void setE_phentsize(int e_phentsize) {
        this.e_phentsize = e_phentsize;
    }

    public int getE_phnum() {
        return e_phnum;
    }

    public void setE_phnum(int e_phnum) {
        this.e_phnum = e_phnum;
    }

    public int getE_shentsize() {
        return e_shentsize;
    }

    public void setE_shentsize(int e_shentsize) {
        this.e_shentsize = e_shentsize;
    }

    public int getE_shnum() {
        return e_shnum;
    }

    public void setE_shnum(int e_shnum) {
        this.e_shnum = e_shnum;
    }

    public int getE_shstrndx() {
        return e_shstrndx;
    }

    public void setE_shstrndx(int e_shstrndx) {
        this.e_shstrndx = e_shstrndx;
    }

}
