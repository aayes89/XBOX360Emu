/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xbox360emu.FILES;

/**
 *
 * @author Slam
 */
public class Elf64Phdr {

    int p_type;
    int p_flags;
    long p_offset;
    long p_vaddr;
    long p_paddr;
    long p_filesz;
    long p_memsz;
    long p_align;

    public Elf64Phdr(int p_type, int p_flags, long p_offset, long p_vaddr, long p_paddr, long p_filesz, long p_memsz, long p_align) {
        this.p_type = p_type;
        this.p_flags = p_flags;
        this.p_offset = p_offset;
        this.p_vaddr = p_vaddr;
        this.p_paddr = p_paddr;
        this.p_filesz = p_filesz;
        this.p_memsz = p_memsz;
        this.p_align = p_align;
    }

    public int getP_type() {
        return p_type;
    }

    public void setP_type(int p_type) {
        this.p_type = p_type;
    }

    public int getP_flags() {
        return p_flags;
    }

    public void setP_flags(int p_flags) {
        this.p_flags = p_flags;
    }

    public long getP_offset() {
        return p_offset;
    }

    public void setP_offset(long p_offset) {
        this.p_offset = p_offset;
    }

    public long getP_vaddr() {
        return p_vaddr;
    }

    public void setP_vaddr(long p_vaddr) {
        this.p_vaddr = p_vaddr;
    }

    public long getP_paddr() {
        return p_paddr;
    }

    public void setP_paddr(long p_paddr) {
        this.p_paddr = p_paddr;
    }

    public long getP_filesz() {
        return p_filesz;
    }

    public void setP_filesz(long p_filesz) {
        this.p_filesz = p_filesz;
    }

    public long getP_memsz() {
        return p_memsz;
    }

    public void setP_memsz(long p_memsz) {
        this.p_memsz = p_memsz;
    }

    public long getP_align() {
        return p_align;
    }

    public void setP_align(long p_align) {
        this.p_align = p_align;
    }

}
