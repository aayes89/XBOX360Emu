package xbox360emu.FILES;

/**
 *
 * @author Slam
 */
public class Elf32Phdr {

    int p_type;
    int p_offset;
    int p_vaddr;
    int p_paddr;
    int p_filesz;
    int p_memsz;
    int p_flags;
    int p_align;

    public Elf32Phdr() {
    }

    public Elf32Phdr(int p_type, int p_offset, int p_vaddr, int p_paddr, int p_filesz, int p_memsz, int p_flags, int p_align) {
        this.p_type = p_type;
        this.p_offset = p_offset;
        this.p_vaddr = p_vaddr;
        this.p_paddr = p_paddr;
        this.p_filesz = p_filesz;
        this.p_memsz = p_memsz;
        this.p_flags = p_flags;
        this.p_align = p_align;
    }

    public int getP_type() {
        return p_type;
    }

    public void setP_type(int p_type) {
        this.p_type = p_type;
    }

    public int getP_offset() {
        return p_offset;
    }

    public void setP_offset(int p_offset) {
        this.p_offset = p_offset;
    }

    public int getP_vaddr() {
        return p_vaddr;
    }

    public void setP_vaddr(int p_vaddr) {
        this.p_vaddr = p_vaddr;
    }

    public int getP_paddr() {
        return p_paddr;
    }

    public void setP_paddr(int p_paddr) {
        this.p_paddr = p_paddr;
    }

    public int getP_filesz() {
        return p_filesz;
    }

    public void setP_filesz(int p_filesz) {
        this.p_filesz = p_filesz;
    }

    public int getP_memsz() {
        return p_memsz;
    }

    public void setP_memsz(int p_memsz) {
        this.p_memsz = p_memsz;
    }

    public int getP_flags() {
        return p_flags;
    }

    public void setP_flags(int p_flags) {
        this.p_flags = p_flags;
    }

    public int getP_align() {
        return p_align;
    }

    public void setP_align(int p_align) {
        this.p_align = p_align;
    }

}
