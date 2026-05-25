package com.start.craftbox.Entity.FileManager;

import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.start.craftbox.R;

import java.sql.Date;
import java.util.List;
import java.util.Locale;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {

    private final List<FileItem> fileList;
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;
    private final SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public interface OnItemClickListener {
        void onItemClick(FileItem item, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(FileItem item, int position);
    }

    public FileAdapter(List<FileItem> fileList) {
        this.fileList = fileList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        FileItem item = fileList.get(position);
        holder.tvName.setText(item.getName());
        holder.imgIcon.setImageResource(getFileIconResource(item));
        String dateStr = timeFormatter.format(new Date(item.getLastModified()));
        if (item.isDirectory()) {
            holder.tvDetail.setText(dateStr);
        } else {
            String sizeStr = formatFileSize(item.getSize());
            String detailText = dateStr + "\n" + sizeStr;
            holder.tvDetail.setText(detailText);
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(item, position);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(item, position);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return fileList != null ? fileList.size() : 0;
    }

    private String formatFileSize(long size) {
        if (size <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return String.format(Locale.getDefault(), "%.2f %s",
                size / Math.pow(1024, digitGroups), units[digitGroups]);
    }

    private String getFileExtension(String fileName) {
        if (fileName == null) return "";
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0 && lastDot < fileName.length() - 1) {
            return fileName.substring(lastDot + 1).toLowerCase(Locale.getDefault());
        }
        return "";
    }

    private int getFileIconResource(FileItem item) {
        if (item.isDirectory()) return R.drawable.folder_open_24px;
        String ext = getFileExtension(item.getName());
        switch (ext) {
            case "zip":
            case "rar":
            case "7z":
            case "tar":
            case "gz":
                return R.drawable.folder_zip_24px;
            case "apk":
            case "apks":
            case "xapk":
                return R.drawable.apk_document_24px;
            case "dex":
                return R.drawable.tabs_24px;
            case "txt":
            case "log":
                return R.drawable.description_24px;
            case "json":
            case "xml":
            case "java":
            case "cpp":
            case "c":
            case "smali":
                return R.drawable.data_object_24px;
            case "mp3":
            case "wav":
            case "flac":
            case "ogg":
                return R.drawable.audio_file_24px;
            case "mp4":
            case "mkv":
            case "avi":
            case "webm":
                return R.drawable.video_file_24px;
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
            case "webp":
                return R.drawable.file_image;
            default:
                return R.drawable.draft_24px;
        }
    }

    static class FileViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView tvName;
        TextView tvDetail;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.img_file_icon);
            tvName = itemView.findViewById(R.id.tv_file_name);
            tvDetail = itemView.findViewById(R.id.tv_file_detail);
        }
    }
}